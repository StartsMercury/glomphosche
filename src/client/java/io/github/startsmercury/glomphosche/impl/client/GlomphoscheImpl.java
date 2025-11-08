package io.github.startsmercury.glomphosche.impl.client;

import io.github.startsmercury.glomphosche.impl.client.node.BasicRootNode;
import io.github.startsmercury.glomphosche.impl.client.node.CompositeNode;
import io.github.startsmercury.glomphosche.impl.client.node.DiscreteNode;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable.ComposableHangulJamoNode;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.ResourceLocation;

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

    static {
        ROOT.inner().add(LOOKUP);
        ROOT.inner().add(new ComposableHangulJamoNode());

        final var tglg = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/default"));
        for (var cp = 0x1712; cp <= 0x1715; cp++) {
            LOOKUP.computeDiscreteIfAbsent(cp).first().fontOverride(tglg);
        }

        final var tglgIi = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/ii"));
        final var tglgUo = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/uo"));
        final var tglgKrus = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/krus"));
        final var tglgPamudpod = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/pamudpod"));

        "ᜀᜁᜂᜃᜄᜅᜆᜇᜈᜉᜊᜋᜌᜍᜎᜏᜐᜑᜟ".codePoints().forEach(cp -> {
            final var node = LOOKUP.computeDiscreteIfAbsent(cp).first();
            node.fontOverride(tglg);
            node.computeDiscreteIfAbsent(0x1712).first().fontOverride(tglgIi);
            node.computeDiscreteIfAbsent(0x1713).first().fontOverride(tglgUo);
            node.computeDiscreteIfAbsent(0x1714).first().fontOverride(tglgKrus);
            node.computeDiscreteIfAbsent(0x1715).first().fontOverride(tglgPamudpod);
        });
    }

    public static int moveCursorSkipZeroWidths(
        final CompletableFuture<int[]> completable,
        final String value,
        final int relative,
        final int cursor
    ) {
        if (relative == 0 || !completable.isDone()) {
            return cursor;
        }

        final var positions = completable.join();
        final var index = Arrays.binarySearch(positions, cursor);
        if (index >= 0) {
            return cursor;
        }

        final var safe = ~index;
        if (safe == 0) {
            return 0;
        } else if (relative < 0) {
            return positions[safe - 1];
        } else if (safe >= positions.length) {
            return value.length();
        } else {
            return positions[safe];
        }
    }
}
