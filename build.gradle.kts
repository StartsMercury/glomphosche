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

    modImplementation(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
    include(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
}

tasks {
    val validateMixinName by registering(net.fabricmc.loom.task.ValidateMixinNameTask::class) {
        source(sourceSets.main.get().output)
        source(sourceSets.named("client").get().output)
    }

    withType<ProcessResources> {
        val data = mapOf(
            "gameVersion" to libs.versions.fabric.minecraft.get(),
            "javaVersion" to libs.versions.java.get(),
            "minecraftVersion" to libs.versions.minecraft.get(),
            "version" to version,
        )

        inputs.properties(data)

        filesMatching("fabric.mod.json") {
            expand(data)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    javadoc {
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

    jar {
        manifest {
            attributes(mapOf(
                "Implementation-Title" to Constants.MOD_NAME,
                "Implementation-Version" to Constants.MOD_VERSION,
                "Implementation-Vendor" to "StartsMercury",
            ))
        }
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
