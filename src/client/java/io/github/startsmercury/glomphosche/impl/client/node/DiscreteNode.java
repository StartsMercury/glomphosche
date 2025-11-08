package io.github.startsmercury.glomphosche.impl.client.node;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;

public class DiscreteNode extends BasicNode {
    private final Int2ObjectOpenHashMap<Node> branches = new Int2ObjectOpenHashMap<>();

    @Override
    public Optional<Node> visit(final int codepoint) {
        return Optional.ofNullable(branches.get(codepoint));
    }

    public Pair<DiscreteNode, Optional<Node>> computeDiscreteIfAbsent(final int codepoint) {
        final var value = this.branches.get(codepoint);
        if (value instanceof final DiscreteNode branch) {
            return Pair.of(branch, Optional.empty());
        } else {
            final var branch = new DiscreteNode();
            this.branches.put(codepoint, branch);
            return Pair.of(branch, Optional.ofNullable(value));
        }
    }

    public <T extends Node> Pair<T, Optional<Node>> computeIfAbsent(
        final int codepoint,
        final Class<T> type,
        final Int2ObjectFunction<T> provider
    ) {
        final var value = this.branches.get(codepoint);
        if (type.isInstance(value)) {
            @SuppressWarnings("unchecked")
            final var branch = (T) value;
            return Pair.of(branch, Optional.empty());
        } else {
            final var branch = provider.apply(codepoint);
            this.branches.put(codepoint, branch);
            return Pair.of(branch, Optional.ofNullable(value));
        }
    }

    public Node put(final int codepoint, final Node node) {
        return this.branches.put(codepoint, node);
    }
}
