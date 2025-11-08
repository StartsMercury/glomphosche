package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;
import java.util.OptionalInt;

public record JongseongNode(int choseong, int jungseong, int jongseong) implements DefaultNode {
    public static final int MIN = 0x11A8;
    public static final int MAX = 0x11C2;

    @Override
    public Optional<Node> visit(final int codepoint) {
        return Optional.empty();
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return OptionalInt.of(588 * choseong + 28 * jungseong + jongseong + 44033);
    }
}
