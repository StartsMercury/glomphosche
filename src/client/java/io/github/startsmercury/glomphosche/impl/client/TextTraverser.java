package io.github.startsmercury.glomphosche.impl.client;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;

public class TextTraverser {
    public static final CompletableFuture<int[]> NO_POSITIONS =
        CompletableFuture.failedFuture(new RuntimeException());

    private CompletableFuture<int[]> positions = NO_POSITIONS;

    public void invalidatePositions(final String value) {
        this.positions = CompletableFuture.supplyAsync(() -> {
            final var posSink = new FormattedCharPosSink();
            try (final var glomphoscheSink = Minecraft.getInstance().getGlomphosche().forSink(posSink)) {
                StringDecomposer.iterate(value, Style.EMPTY, glomphoscheSink);
            }
            return posSink.positions().toIntArray();
        });
    }

    /**
     * Calculates cursor position at grapheme boundaries.
     *
     * @param length  The length of the string value. The max cursor position.
     * @param cursor  The cursor. The initial cursor pre-added with the dir.
     * @param dir  The direction the cursor took.
     * @return The cursor position at a grapheme boundary.
     */
    public int offsetByGraphemes(
        final int length,
        final int cursor,
        final int dir
    ) {
        final var completable = this.positions;
        if (dir == 0 || !completable.isDone()) {
            return cursor;
        }

        final var positions = completable.join();
        if (positions.length == 0) {
            return cursor;
        }

        final var key = Arrays.binarySearch(positions, cursor);
        if (key >= 0) {
            return cursor;
        }

        final var index = ~key;
        if (dir < 0) {
            return index == 0 ? 0 : positions[index - 1];
        } else {
            return index >= positions.length ? length : positions[index];
        }
    }
}
