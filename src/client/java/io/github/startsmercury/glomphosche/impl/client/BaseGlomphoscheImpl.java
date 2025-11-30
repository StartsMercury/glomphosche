package io.github.startsmercury.glomphosche.impl.client;

import io.github.startsmercury.glomphosche.impl.client.node.BasicRootNode;
import io.github.startsmercury.glomphosche.impl.client.node.CompositeNode;
import io.github.startsmercury.glomphosche.impl.client.node.DiscreteNode;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSink;

class BaseGlomphoscheImpl {
    private static final String MODID = "glomphosche";

    public static ResourceLocation withDefaultNamespace(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private final BasicRootNode<CompositeNode> root = new BasicRootNode<>(new CompositeNode());
    private final DiscreteNode lookup = new DiscreteNode();
    {
        this.root.inner().add(this.lookup);
    }

    /**
     * The root composite node.
     * <p>
     * This the most technical node and combines the {@code lookup} and possibly
     * other types of nodes as one root, that is, fallbacks. Visit attempts are
     * done from first added to last, which means, {@code lookup}, by default,
     * is checked first.
     *
     * @see #lookup
     */
    public BasicRootNode<CompositeNode> root() {
        return this.root;
    }

    /**
     * The lookup co-root node.
     * <p>
     * This is the simplest you can get your own nodes registered. Each
     * registration will map to one codepoint, further registration on that node
     * will represent a codepoint sequence to match and will match and consume
     * that sequence when you register either one or both override types.
     */
    public DiscreteNode lookup() {
        return this.lookup;
    }

    public GlomphoschingFormattedCharSink forSink(final FormattedCharSink sink) {
        return new GlomphoschingFormattedCharSink(sink, this.root());
    }

    private final FabricLoader fabricLoader;
    private final Minecraft minecraft;

    public BaseGlomphoscheImpl(final Minecraft minecraft) {
        this.minecraft = minecraft;
        this.fabricLoader = FabricLoader.getInstance();
    }

    public void initialize() {
        final var container = this
            .fabricLoader
            .getModContainer(MODID)
            .orElseThrow(() -> new RuntimeException(
                "Glomphosche was not present through FabricLoader.getModContainer. Perhaps BaseGlomphoscheImpl.initialize was called before FabricLoader had properly initialized."
            ));
        registerNormalBuiltinResourcePack(container, "glomphosche-tglg");
        registerNormalBuiltinResourcePack(container, "glomphosche-hnno");
    }

    private static void registerNormalBuiltinResourcePack(final ModContainer container, final String path) {
        ResourceManagerHelper.registerBuiltinResourcePack(
            withDefaultNamespace(path),
            container,
            Component.translatable(MODID + ".resourcePack." + path + ".name"),
            ResourcePackActivationType.NORMAL
        );
    }
}
