package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;
import java.util.OptionalInt;

public record JongseongNode(int choseong, int jungseong, int jongseong) implements DefaultNode {
    public static final int MODERN_START = 0x11A8;
    public static final int MODERN_END = 0x11C2;
    public static final int COUNT = 1 + MODERN_END - MODERN_START;

    static Optional<Node> of(final int choseong, final int jungseong, final int jongseong) {
        if (MODERN_START <= jongseong && jongseong <= MODERN_END) {
            return Optional.of(new JongseongNode(choseong, jungseong, 1 + jongseong - MODERN_START));
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
        return OptionalInt.of(ModernHangulJamos.composeFromIndices(choseong, jungseong, jongseong));
    }
}
