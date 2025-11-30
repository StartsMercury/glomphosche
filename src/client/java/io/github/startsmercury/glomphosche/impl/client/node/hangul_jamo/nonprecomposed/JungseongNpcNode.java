package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed;

import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.network.chat.FontDescription;

public record JungseongNpcNode(int choseong, int jungseong) implements DefaultNode {
    public static int JAMO_START = 0x1161;
    public static int JAMO_END = 0x11A7;

    public static int JAMO_EXTENDED_START = 0xD7B0;
    public static int JAMO_EXTENDED_END = 0xD7C6;

    public static Optional<Node> of(final int choseong, final int jungseong) {
        if (
            // Prioritize this vowel used in Jejuan when combined with modern jamo.
            jungseong == 'ᆞ'
            // FIXME: use this when we DO have support for ALL syllables
            // AMO_START <= jungseong && jungseong <= JAMO_END
            //     || JAMO_EXTENDED_START <= jungseong && jungseong <= JAMO_EXTENDED_END
        ) {
            return Optional.of(new JungseongNpcNode(choseong, jungseong));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Node> visit(final int codepoint) {
        return JongseongNpcNode.of(choseong, jungseong, codepoint);
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return OptionalInt.of(jungseong);
    }

    @Override
    public Optional<FontDescription> getFontOverride() {
        return Optional.of(
            new FontDescription.Resource(
                GlomphoscheImpl.withDefaultNamespace("hangul_jamo/%04x/default".formatted(choseong))
            )
        );
    }
}
