package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.nonprecomposed.ChoseongNpcNode;
import java.util.Optional;

/**
 * Composable Hangul Jamo node.
 */
public class HangulJamoStartNode implements DefaultNode {
    @Override
    public Optional<Node> visit(final int codepoint) {
        final var first = ChoseongNode.of(codepoint);
        if (first.isPresent()) return first;
        return ChoseongNpcNode.of(codepoint);
    }
}
