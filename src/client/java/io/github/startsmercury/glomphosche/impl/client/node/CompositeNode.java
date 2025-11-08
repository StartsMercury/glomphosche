package io.github.startsmercury.glomphosche.impl.client.node;

import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import java.util.Optional;
import java.util.stream.Stream;

public class CompositeNode extends BasicNode {
    private final ReferenceLinkedOpenHashSet<Node> registry
        = new ReferenceLinkedOpenHashSet<>();

    @Override
    public Optional<Node> visit(final int codepoint) {
        for (final var node : registry) {
            final var branch = node.visit(codepoint);
            if (branch.isPresent()) {
                return branch;
            }
        }
        return Optional.empty();
    }

    public Stream<Node> stream() {
        return this.registry.stream();
    }

    public Stream<Node> stream(final int codepoint) {
        return this
            .registry
            .stream()
            .flatMap(node -> node.visit(codepoint).stream());
    }

    public boolean add(final Node node) {
        return this.registry.add(node);
    }

    public boolean contains(final Node node) {
        return this.registry.contains(node);
    }

    public boolean remove(final Node node) {
        return this.registry.remove(node);
    }
}
