package io.github.startsmercury.glomphosche.impl.client.extension;

import io.github.startsmercury.glomphosche.impl.client.TextTraverser;

public interface TextTraverserAware {
    TextTraverser glomphosche$getTraverser();

    String glomphosche$getValue();

    default void glomphosche$invalidateTraverser() {
        this.glomphosche$invalidateTraverser(this.glomphosche$getValue());
    }

    default void glomphosche$invalidateTraverser(final String value) {
        this.glomphosche$getTraverser().invalidatePositions(value);
    }
}
