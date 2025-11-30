package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed.JongseongNpcNode;
import java.util.Optional;
import java.util.OptionalInt;

public record JungseongNode(int choseong, int jungseong) implements DefaultNode {
    public static final int MODERN_START = 0x1161;
    public static final int MODERN_END = 0x1175;
    public static final int COUNT = 1 + MODERN_END - MODERN_START;

    static Optional<Node> of(final int choseong, final int jungseong) {
        if (MODERN_START <= jungseong && jungseong <= MODERN_END) {
            return Optional.of(new JungseongNode(choseong, jungseong - MODERN_START));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Node> visit(final int codepoint) {
        final var first = JongseongNode.of(choseong, jungseong, codepoint);
        if (first.isPresent()) return first;
        return JongseongNpcNode.of(
            choseong + ChoseongNode.MODERN_START,
            jungseong + MODERN_START,
            codepoint
        );
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return OptionalInt.of(ModernHangulJamos.composeFromIndices(choseong, jungseong, 0));
    }
}
