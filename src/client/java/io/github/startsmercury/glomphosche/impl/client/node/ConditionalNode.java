package io.github.startsmercury.glomphosche.impl.client.node;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.FontDescription;

/**
 * @param inner
 * @param predicate  Determines node to forward to inner, if true.
 * @param <T>
 */
public record ConditionalNode<T extends Node>(
    T inner,
    BooleanSupplier predicate
) implements Node {
    @Override
    public Optional<Node> visit(final int codepoint) {
        if (this.predicate.getAsBoolean()) {
            return this.inner.visit(codepoint);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public OptionalInt getCodepointOverride() {
        if (this.predicate.getAsBoolean()) {
            return this.inner.getCodepointOverride();
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public Optional<FontDescription> getFontOverride() {
        if (this.predicate.getAsBoolean()) {
            return this.inner.getFontOverride();
        } else {
            return Optional.empty();
        }
    }
}
