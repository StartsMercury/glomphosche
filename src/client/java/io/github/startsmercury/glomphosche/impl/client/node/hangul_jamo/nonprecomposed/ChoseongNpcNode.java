package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;

public record ChoseongNpcNode(int choseong) implements DefaultNode {
    public static int JAMO_START = 0x1100;
    public static int JAMO_END = 0x1159;

    public static int JAMO_EXTENDED_START = 0xA960;
    public static int JAMO_EXTENDED_END = 0xA97D;

    public static Optional<Node> of(final int choseong) {
        if (
            JAMO_START <= choseong && choseong <= JAMO_END
                || JAMO_EXTENDED_START <= choseong && choseong <= JAMO_EXTENDED_END
        ) {
            return Optional.of(new ChoseongNpcNode(choseong));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Node> visit(final int codepoint) {
        return JungseongNpcNode.of(choseong, codepoint);
    }
}
