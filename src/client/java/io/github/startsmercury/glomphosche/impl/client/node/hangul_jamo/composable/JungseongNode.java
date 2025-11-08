package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;
import java.util.OptionalInt;

public record JungseongNode(int choseong, int jungseong) implements DefaultNode {
    public static final int MIN = 0x1161;
    public static final int MAX = 0x1175;

    @Override
    public Optional<Node> visit(final int codepoint) {
        if (JongseongNode.MIN <= codepoint && codepoint <= JongseongNode.MAX) {
            return Optional.of(new JongseongNode(choseong, jungseong, codepoint - JongseongNode.MIN));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return OptionalInt.of(588 * choseong + 28 * jungseong + 44032);
    }
}
