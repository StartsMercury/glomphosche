package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed.JungseongNpcNode;
import java.util.Optional;

public record ChoseongNode(int choseong) implements DefaultNode {
    public static final int MODERN_START = 0x1100;
    public static final int MODERN_END = 0x1112;
    public static final int COUNT = 1 + MODERN_END - MODERN_START;

    public static Optional<Node> of(final int choseong) {
        if (MODERN_START <= choseong && choseong <= MODERN_END) {
            return Optional.of(new ChoseongNode(choseong - MODERN_START));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Node> visit(final int codepoint) {
        final var first = JungseongNode.of(choseong, codepoint);
        if (first.isPresent()) return first;
        return JungseongNpcNode.of(choseong + MODERN_START, codepoint);
    }
}
