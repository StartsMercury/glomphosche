package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo.composable;

import io.github.startsmercury.glomphosche.impl.client.node.DefaultNode;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import java.util.Optional;

/**
 * Composable Hangul Jamo node.
 */
public class ComposableHangulJamoNode implements DefaultNode {
    @Override
    public Optional<Node> visit(final int codepoint) {
        if (ChoseongNode.MIN <= codepoint && codepoint <= ChoseongNode.MAX) {
            return Optional.of(new ChoseongNode(codepoint - ChoseongNode.MIN));
        } else {
            return Optional.empty();
        }
    }
}
