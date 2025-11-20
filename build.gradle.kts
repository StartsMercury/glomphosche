import java.nio.file.Path
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.util.TreeMap
import java.util.zip.ZipOutputStream
import kotlin.io.path.copyTo
import kotlin.io.path.createParentDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.readLines
import kotlin.io.path.relativeTo
import kotlin.io.path.visitFileTree
import kotlin.text.split

object Constants {
    const val MOD_NAME: String = "Glomphosche"
    const val MOD_VERSION: String = "0.2.1"
}

plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    group = "io.github.startsmercury"
    archivesName = "glomphosche"
    version = createVersionString()
}

val unifont by sourceSets.creating {}

loom {
    runtimeOnlyLog4j = true
    splitEnvironmentSourceSets()

    mods.register("glomphosche") {
        sourceSet("main")
        sourceSet("client")
    }
}

java {
    toolchain {
        languageVersion.convention(libs.versions.java.map(JavaLanguageVersion::of))
    }

    // withJavadocJar()
    withSourcesJar()
}

repositories {
    maven {
        name = "Terraformers Maven"
        url = uri("https://maven.terraformersmc.com")
        content {
            includeGroup("com.terraformersmc")
        }
    }
}

dependencies {
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)

    modRuntimeOnly(libs.fabric.api)
    modCompileOnly(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
    include(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))

    modRuntimeOnly(libs.modmenu)
}

val validateMixinName by tasks.registering(net.fabricmc.loom.task.ValidateMixinNameTask::class) {
    source(sourceSets.main.get().output)
    source(sourceSets.named("client").get().output)
}

val processUnifontResources by tasks.existing(ProcessResources::class) {}

val unifontToHex by tasks.registering {
    group = "unifont"
    dependsOn(processUnifontResources)
    inputs.files(processUnifontResources)
    val destinationDir = layout.buildDirectory.dir("hex").get().asFile
    outputs.dir(destinationDir)

    doFirst {
        val hexFormat = HexFormat {
            upperCase = true
            number.removeLeadingZeros = true
        }

        val root = inputs.files.singleFile.toPath()
        val files = mutableListOf<Path>()

        root.visitFileTree {
            onVisitFile { file, _ ->
                if (!file.isRegularFile()) {
                    return@onVisitFile FileVisitResult.CONTINUE
                }
                val name = file.name.removeSuffix(".txt")
                if (name.length != 4 && name.length != 6) {
                    logger.info("Unsupported Unifont hex entry name length ${name.length}; expected 4 or 6")
                } else if (name.toUIntOrNull(16) == null) {
                    logger.info("Nonvalid Unifont hex entry hex integer name $name")
                } else {
                    files.add(file)
                }
                FileVisitResult.CONTINUE
            }
        }

        val errors = mutableListOf<Throwable>()

        files.groupBy { it.parent }.forEach { (parent, files) ->
            val hexErrors = mutableListOf<Throwable>()
            val index = TreeMap<String, String>()

            for (file in files) {
                val path = file.relativeTo(root).joinToString("/")
                val lines = file.readLines()
                val glyphErrors = mutableListOf<Throwable>()
                val height = lines.size

                if (height != 16) {
                    glyphErrors.add(RuntimeException("Unsupported Unifont glyph height $height, expected 16"))
                }

                // Assertion, a file can never have zero height/lines
                val width = lines[0].length
                var jagged = false
                for ((i, line) in lines.withIndex()) {
                    val w = line.length
                    if (w == width) continue
                    jagged = true
                    glyphErrors.add(RuntimeException("Unifont glyph width $w at line ${i + 1} is inconsistent to $width"))
                }

                if (!(jagged  || width == 8 || width == 16 || width == 24 || width == 32)) {
                    glyphErrors.add(RuntimeException("Unsupported Unifont glyph width $width, expected either 8, 16, 24 or 32"))
                }

                if (glyphErrors.isNotEmpty()) {
                    val iter = glyphErrors.iterator()
                    val first = iter.next()
                    val error = RuntimeException("Unable to encode Unifont glyph at $path: ${first.message}", first)
                    iter.forEach(error::addSuppressed)
                    hexErrors.add(error)
                    continue
                }

                val hex = lines
                    .asSequence()
                    .flatMap { it.chunkedSequence(4) }
                    .map {
                        (if (it[0] == '@') 8 else 0) +
                            (if (it[1] == '@') 4 else 0) +
                            (if (it[2] == '@') 2 else 0) +
                            (if (it[3] == '@') 1 else 0)
                    }
                    .joinToString("") { it.toHexString(hexFormat) }

                index[file.name.removeSuffix(".txt")] = hex
            }

            val path = parent.relativeTo(root).joinToString("/")

            if (hexErrors.isNotEmpty()) {
                val iter = hexErrors.iterator()
                val first = iter.next()
                val error = RuntimeException(
                    "Unable to compile Unifont hex for $path: ${first.message}",
                    first
                )
                iter.forEach(error::addSuppressed)
                errors.add(error)
            } else if (index.isNotEmpty()) {
                val dest = destinationDir.resolve("$path.hex")
                dest.parentFile.mkdirs()
                dest.writeText(
                    index
                        .asSequence()
                        .map { "${it.key}:${it.value}" }
                        .joinToString("\n", postfix = "\n")
                )
            }
        }

        if (errors.isNotEmpty()) {
            val iter = errors.iterator()
            val first = iter.next()
            val error = RuntimeException(first)
            iter.forEach(error::addSuppressed)
            throw error
        }
    }
}

val zipUnihex by tasks.registering {
    group = "unifont"
    dependsOn(unifontToHex)
    inputs.files(unifontToHex)
    val destinationDir = layout.buildDirectory.dir("hexzip").get().asFile
    outputs.dir(destinationDir)

    doFirst {
        val baseDir = inputs.files.singleFile.toPath()

        baseDir.visitFileTree {
            onVisitFile { file, _ ->
                val hexPath = file.relativeTo(baseDir)
                val hexName = hexPath.pathString
                val hexBaseName = hexName.removeSuffix(".hex")

                // Full name path separated by underscores
                // Include mod version into hex file name
                val rename = hexPath
                    .joinToString("_")
                    .removeSuffix(".hex") +
                    "-" +
                    Constants.MOD_VERSION +
                    ".hex"

                val zipDest = destinationDir.resolve("$hexBaseName.zip")
                zipDest.parentFile.mkdirs()
                // Create a blank ZIP cuz idk
                ZipOutputStream(zipDest.outputStream()).use { }
                // Actually fill the ZIP here cuz easier
                FileSystems.newFileSystem(zipDest.toPath()).use {
                    file.copyTo(it.getPath(rename).createParentDirectories())
                }
                FileVisitResult.CONTINUE
            }
        }
    }
}

val processClientResources by tasks.existing(ProcessResources::class) {
    dependsOn(zipUnihex)

    from(zipUnihex) {
        into("assets/${base.archivesName.get()}/font")
    }
}

tasks.withType<ProcessResources> {
    val fmjData = mapOf(
        "gameVersion" to libs.versions.fabric.minecraft.get(),
        "javaVersion" to libs.versions.java.get(),
        "minecraftVersion" to libs.versions.minecraft.get(),
        "version" to version,
    )
    val fpmData = buildMap {
        val versionString = libs.versions.mcpackformat.get()
        val version = versionString.split(".")
        if (version.size != 1 && version.size != 2) {
            throw RuntimeException("Invalid resource pack format version $versionString")
        }
        put("packFormatLegacy", version[0])
        put("packFormat", version)
    }

    inputs.properties(fmjData)
    inputs.properties(fpmData)

    filesMatching("fabric.mod.json") {
        expand(fmjData)
    }

    filesMatching("**/pack.mcmeta") {
        expand(fpmData)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions

        source = libs.versions.java.get()
        encoding = "UTF-8"
        charSet = "UTF-8"
        memberLevel = JavadocMemberLevel.PRIVATE
        addStringOption("Xdoclint:none", "-quiet")
        tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:",
        )
    }

    source(sourceSets.main.get().allJava)
    source(sourceSets.named("client").get().allJava)
    classpath = files(
        sourceSets.main.get().compileClasspath,
        sourceSets.named("client").get().compileClasspath
    )
    isFailOnError = true
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to Constants.MOD_NAME,
            "Implementation-Version" to Constants.MOD_VERSION,
            "Implementation-Vendor" to "StartsMercury",
        ))
    }
}

fun createVersionString(): String {
    val builder = StringBuilder()

    val isReleaseBuild = project.hasProperty("build.release")
    val buildId = System.getenv("GITHUB_RUN_NUMBER")

    if (isReleaseBuild) {
        builder.append(Constants.MOD_VERSION)
    } else {
        builder.append(Constants.MOD_VERSION.substringBefore('-'))
        builder.append("-snapshot")
    }

    builder.append("+mc").append(libs.versions.minecraft.get())

    if (!isReleaseBuild) {
        if (buildId != null) {
            builder.append("-build.${buildId}")
        } else {
            builder.append("-local")
        }
    }

    return builder.toString()
}
