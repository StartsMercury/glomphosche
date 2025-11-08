package io.github.startsmercury.glomphosche.impl.client;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.ResourceLocation;

// TODO: consider API and or registering through resource packs
public class GlomphoscheImpl {
    /**
     * The chomposing root.
     * <p>
     * Character composing involves changing the font of the starting codepoint.
     * Successive restyling must be different fonts for the same starting
     * codepoint to achieve the desired effect. If so convenient, group the same
     * modification in the same font definition.
     */
    public static final GlomphoscheNode ROOT = new GlomphoscheNode();

    public static final FontDescription.Resource EMPTY_FONT = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "empty"));

    static {
        final var tglg = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/default"));
        for (var cp = 0x1712; cp <= 0x1715; cp++) {
            ROOT.getOrCreate(cp).register(tglg);
        }

        final var tglgIi = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/ii"));
        final var tglgUo = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/uo"));
        final var tglgKrus = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/krus"));
        final var tglgPamudpod = new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("glomphosche", "tagalog/pamudpod"));

        "ᜀᜁᜂᜃᜄᜅᜆᜇᜈᜉᜊᜋᜌᜍᜎᜏᜐᜑᜟ".codePoints().forEach(cp -> {
            final var node = ROOT.getOrCreate(cp);
            node.register(tglg);
            node.getOrCreate(0x1712).register(tglgIi);
            node.getOrCreate(0x1713).register(tglgUo);
            node.getOrCreate(0x1714).register(tglgKrus);
            node.getOrCreate(0x1715).register(tglgPamudpod);
        });

        // Test node traversal without match
        // ROOT.getOrCreate('ᜊ').getOrCreate('ᜊ');
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
