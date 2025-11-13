package io.github.startsmercury.glomphosche.impl.client.node;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

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

    public DiscreteNode withDiscrete(final int codepoint) {
        return this.computeDiscreteIfAbsent(codepoint).first();
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

    public <T extends Node> Pair<T, Optional<Node>> computeIfAbsent(
        final int codepoint,
        final Int2ObjectFunction<T> provider,
        final BinaryOperator<T> merger,
        final BiFunction<T, Node, T> replacer
    ) {
        var node = provider.apply(codepoint);
        final Optional<Node> existingOption;
        if (!this.branches.containsKey(codepoint)) {
            existingOption = Optional.empty();
        } else {
            final var existing = this.branches.get(codepoint);
            if (node.getClass().isInstance(existing)) {
                @SuppressWarnings("unchecked")
                final var other = (T) existing;
                node = merger.apply(node, other);
            } else {
                node = replacer.apply(node, existing);
            }
            existingOption = Optional.of(existing);
        }
        return Pair.of(node, existingOption);
    }

    public <T extends Node> T withGeneric(
        final int codepoint,
        final Class<T> type,
        final Int2ObjectFunction<T> provider
    ) {
        return this.computeIfAbsent(codepoint, type, provider).first();
    }

    public <T extends Node> T withGeneric(
        final int codepoint,
        final Int2ObjectFunction<T> provider,
        final BinaryOperator<T> merger,
        final BiFunction<T, Node, T> replacer
    ) {
        return this.computeIfAbsent(codepoint, provider, merger, replacer).first();
    }

    public Optional<Node> put(final int codepoint, final Node node) {
        return Optional.ofNullable(this.branches.put(codepoint, node));
    }

    public <T extends Node> T withNode(final int codepoint, final T node) {
        this.branches.put(codepoint, node);
        return node;
    }

    public BasicNode withEnd(final int codepoint) {
        return this.withNode(codepoint, new BasicNode());
    }
}
