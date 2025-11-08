package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;

public record ChoseongNode(int choseong) implements DefaultNode {
    public static final int MIN = 0x1100;
    public static final int MAX = 0x1112;

    @Override
    public Optional<Node> visit(final int codepoint) {
        if (JungseongNode.MIN <= codepoint && codepoint <= JungseongNode.MAX) {
            return Optional.of(new JungseongNode(choseong, codepoint - JungseongNode.MIN));
        } else {
            return Optional.empty();
        }
    }
}
