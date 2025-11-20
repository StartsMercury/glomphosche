package io.github.startsmercury.glomphosche.impl.client;

import io.github.startsmercury.glomphosche.impl.client.node.BasicNode;
import io.github.startsmercury.glomphosche.impl.client.node.ConditionalNode;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable.ComposableHangulJamoNode;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.util.FormattedCharSink;

// TODO: consider API and or registering through resource packs
public class GlomphoscheImpl extends BaseGlomphoscheImpl {
    private boolean composeWithBaybayinVowels =
        Boolean.getBoolean("glomphosche.composeWithBaybayinVowels");
    public boolean composeWithBaybayinVowels() { return composeWithBaybayinVowels; }
    public void composeWithBaybayinVowels(final boolean b) { composeWithBaybayinVowels = b; }

    private static FontDescription.Resource builtinFontAt(final String path) {
        return new FontDescription.Resource(BaseGlomphoscheImpl.withDefaultNamespace(path));
    }

    public GlomphoscheImpl(final Minecraft minecraft) {
        super(minecraft);
        this.root().inner().add(new ComposableHangulJamoNode());
        registerTagalog();
        registerHanunoo();
    }

    private void registerTagalog() {
        final var tagalog = builtinFontAt("tagalog/default");
        for (var i = 0x1712; i <= 0x1715; i++) {
            this.lookup().withDiscrete(i).fontOverride(tagalog);
        }

        final var tagalogModifiers = List.of(
            IntObjectPair.of(0x1712, builtinFontAt("tagalog/ii")),
            IntObjectPair.of(0x1713, builtinFontAt("tagalog/uo")),
            IntObjectPair.of(0x1714, builtinFontAt("tagalog/krus")),
            IntObjectPair.of(0x1715, builtinFontAt("tagalog/pamudpod"))
        );

        for (var i = 0x1700; i <= 0x1702; i++) {
            final var node = this.lookup().withDiscrete(i);
            node.fontOverride(tagalog);
            for (final var modifier : tagalogModifiers) {
                final var inner = new BasicNode();
                inner.fontOverride(modifier.second());
                node.withNode(
                    modifier.firstInt(),
                    new ConditionalNode<>(inner, this::composeWithBaybayinVowels)
                );
            }
        }

        IntStream.concat(
            IntStream.rangeClosed(0x1703, 0x1711),
            IntStream.of(0x171f)
        ).forEach(cp -> {
            final var node = this.lookup().withDiscrete(cp);
            node.fontOverride(tagalog);
            for (final var modifier : tagalogModifiers) {
                node.withEnd(modifier.firstInt()).fontOverride(modifier.second());
            }
        });
    }

    private void registerHanunoo() {
        final var hanunoo = builtinFontAt("hanunoo/default");
        for (var cp = 0x1732; cp <= 0x1736; cp++) {
            this.lookup().withDiscrete(cp).fontOverride(hanunoo);
        }

        final var hanunooModifiers = List.of(
            IntObjectPair.of(0x1732, builtinFontAt("hanunoo/ii")),
            IntObjectPair.of(0x1733, builtinFontAt("hanunoo/uo")),
            IntObjectPair.of(0x1734, builtinFontAt("hanunoo/pamudpod"))
        );

        for (var cp = 0x1720; cp <= 0x1722; cp++) {
            final var node = this.lookup().withDiscrete(cp);
            node.fontOverride(hanunoo);
            for (final var modifier : hanunooModifiers) {
                final var inner = new BasicNode();
                inner.fontOverride(modifier.second());
                node.withNode(
                    modifier.firstInt(),
                    new ConditionalNode<>(inner, this::composeWithBaybayinVowels)
                );
            }
        }

        for (var cp = 0x1723; cp <= 0x1731; cp++) {
            final var node = this.lookup().withDiscrete(cp);
            node.fontOverride(hanunoo);
            for (final var modifier : hanunooModifiers) {
                node.withDiscrete(modifier.firstInt()).fontOverride(modifier.second());
            }
        }
    }

    public static boolean withWrappedSink(
        final FormattedCharSink sink,
        final Object2BooleanFunction<GlomphoschingFormattedCharSink> action
    ) {
        final var sink2 = Minecraft.getInstance().getGlomphosche().forSink(sink);
        final boolean result;
        try (sink2) {
            result = action.apply(sink2);
        }
        return result && sink2.pop();
    }
}
