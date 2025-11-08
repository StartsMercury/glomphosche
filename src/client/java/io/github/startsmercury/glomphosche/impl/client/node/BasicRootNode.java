package io.github.startsmercury.glomphosche.impl.client.node;

import java.util.Optional;

public record BasicRootNode<T extends Node>(T inner) implements DefaultNode {
    @Override
    public Optional<Node> visit(final int codepoint) {
        return this.inner.visit(codepoint);
    }
}
