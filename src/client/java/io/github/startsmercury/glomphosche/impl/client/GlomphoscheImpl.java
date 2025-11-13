package io.github.startsmercury.glomphosche.impl.client;

import io.github.startsmercury.glomphosche.impl.client.node.BasicNode;
import io.github.startsmercury.glomphosche.impl.client.node.BasicRootNode;
import io.github.startsmercury.glomphosche.impl.client.node.CompositeNode;
import io.github.startsmercury.glomphosche.impl.client.node.ConditionalNode;
import io.github.startsmercury.glomphosche.impl.client.node.DiscreteNode;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable.ComposableHangulJamoNode;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSink;

// TODO: consider API and or registering through resource packs
public class GlomphoscheImpl {
    /**
     * The root composite node.
     * <p>
     * This the most technical node and combines the {@code LOOKUP} and possibly
     * other types of nodes as one root, that is, fallbacks. Visit attempts are
     * done from first added to last, which means, {@code LOOKUP}, by default,
     * is checked first.
     *
     * @see #LOOKUP
     */
    public static final BasicRootNode<CompositeNode> ROOT = new BasicRootNode<>(new CompositeNode());

    /**
     * The lookup co-root node.
     * <p>
     * This is the simplest you can get your own nodes registered. Each
     * registration will map to one codepoint, further registration on that node
     * will represent a codepoint sequence to match and will match and consume
     * that sequence when you register either one or both override types.
     */
    public static final DiscreteNode LOOKUP = new DiscreteNode();

    private static boolean composeWithBaybayinVowels =
        Boolean.getBoolean("glomphosche.composeWithBaybayinVowels");
    public static boolean composeWithBaybayinVowels() { return composeWithBaybayinVowels; }
    public static void composeWithBaybayinVowels(final boolean b) { composeWithBaybayinVowels = b; }

    static {
        ROOT.inner().add(LOOKUP);
        ROOT.inner().add(new ComposableHangulJamoNode());
        registerTagalog();
        registerHanunoo();
    }

    private static void registerTagalog() {
        final var tagalog = builtinFontAt("tagalog/default");
        for (var i = 0x1712; i <= 0x1715; i++) {
            LOOKUP.withDiscrete(i).fontOverride(tagalog);
        }

        final var tagalogModifiers = List.of(
            IntObjectPair.of(0x1712, builtinFontAt("tagalog/ii")),
            IntObjectPair.of(0x1713, builtinFontAt("tagalog/uo")),
            IntObjectPair.of(0x1714, builtinFontAt("tagalog/krus")),
            IntObjectPair.of(0x1715, builtinFontAt("tagalog/pamudpod"))
        );

        for (var i = 0x1700; i <= 0x1702; i++) {
            final var node = LOOKUP.withDiscrete(i);
            node.fontOverride(tagalog);
            for (final var modifier : tagalogModifiers) {
                final var inner = new BasicNode();
                inner.fontOverride(modifier.second());
                node.withNode(
                    modifier.firstInt(),
                    new ConditionalNode<>(inner, GlomphoscheImpl::composeWithBaybayinVowels)
                );
            }
        }

        IntStream.concat(
            IntStream.rangeClosed(0x1703, 0x1711),
            IntStream.of(0x171f)
        ).forEach(cp -> {
            final var node = LOOKUP.withDiscrete(cp);
            node.fontOverride(tagalog);
            for (final var modifier : tagalogModifiers) {
                node.withEnd(modifier.firstInt()).fontOverride(modifier.second());
            }
        });
    }

    private static void registerHanunoo() {
        final var hanunoo = builtinFontAt("hanunoo/default");
        for (var cp = 0x1732; cp <= 0x1736; cp++) {
            LOOKUP.withDiscrete(cp).fontOverride(hanunoo);
        }

        final var hanunooModifiers = List.of(
            IntObjectPair.of(0x1732, builtinFontAt("hanunoo/ii")),
            IntObjectPair.of(0x1733, builtinFontAt("hanunoo/uo")),
            IntObjectPair.of(0x1734, builtinFontAt("hanunoo/pamudpod"))
        );

        for (var cp = 0x1720; cp <= 0x1722; cp++) {
            final var node = LOOKUP.withDiscrete(cp);
            node.fontOverride(hanunoo);
            for (final var modifier : hanunooModifiers) {
                final var inner = new BasicNode();
                inner.fontOverride(modifier.second());
                node.withNode(
                    modifier.firstInt(),
                    new ConditionalNode<>(inner, GlomphoscheImpl::composeWithBaybayinVowels)
                );
            }
        }

        for (var cp = 0x1723; cp <= 0x1731; cp++) {
            final var node = LOOKUP.withDiscrete(cp);
            node.fontOverride(hanunoo);
            for (final var modifier : hanunooModifiers) {
                node.withDiscrete(modifier.firstInt()).fontOverride(modifier.second());
            }
        }
    }

    private static FontDescription.Resource builtinFontAt(final String path) {
        return new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", path));
    }

    public static boolean withWrappedSink(
        final FormattedCharSink sink,
        final Object2BooleanFunction<GlomphoschingFormattedCharSink> action
    ) {
        final var sink2 = new GlomphoschingFormattedCharSink(sink, ROOT);
        final boolean result;
        try (sink2) {
            result = action.apply(sink2);
        }
        return result && sink2.pop();
    }
}
