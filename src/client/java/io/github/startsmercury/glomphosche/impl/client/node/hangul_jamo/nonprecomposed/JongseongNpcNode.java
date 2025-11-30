package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed;

import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;
import java.util.OptionalInt;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.JongseongNode;
import net.minecraft.network.chat.FontDescription;

public record JongseongNpcNode(int choseong, int jungseong, int jongseong) implements DefaultNode {
    public static int JAMO_START = 0x11A8;
    public static int JAMO_END = 0x11FF;

    public static int JAMO_EXTENDED_START = 0xD7CB;
    public static int JAMO_EXTENDED_END = 0xD7FB;

    public static Optional<Node> of(int choseong, int jungseong, int jongseong) {
        // We'll support just the modern ones with this vowel for now.
        if (jungseong == 'ᆞ' && JongseongNode.MODERN_START <= jongseong && jongseong <= JongseongNode.MODERN_END) {
        // FIXME: use this when we DO have support for ALL syllables
        // if (
        //     JAMO_START <= jongseong && jongseong <= JAMO_END
        //         || JAMO_EXTENDED_START <= jongseong && jongseong <= JAMO_EXTENDED_END
        // ) {
            return Optional.of(new JongseongNpcNode(choseong, jungseong, jongseong));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Node> visit(final int codepoint) {
        return Optional.empty();
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return OptionalInt.of(jongseong);
    }

    @Override
    public Optional<FontDescription> getFontOverride() {
        return Optional.of(
            new FontDescription.Resource(
                GlomphoscheImpl.withDefaultNamespace(
                    "hangul_jamo/%04x/%04x".formatted(choseong, jungseong)
                )
            )
        );
    }
}
