package io.github.startsmercury.glomphosche.impl.client;

import static io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl.EMPTY_FONT;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Optional;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

/**
 * A buffered formatted character composing sink.
 * <p>
 * Failing, that is the delegate sink returning {@code false}, will discard all
 * buffered data.
 */
public final class GlomphoschingFormattedCharSink implements AutoCloseable, FormattedCharSink {
    private final FormattedCharSink sink;
    private final GlomphoscheNode root;

    private ObjectList<Capture> captures = new ObjectArrayList<>();
    private Optional<FontDescription.Resource> candidate = Optional.empty();
    private boolean success = true;
    private GlomphoscheNode tail;
    private int index;

    // Use unused font as initial cache mapping. Cant predict the mapped font.
    private FontDescription.Resource cachedFont = EMPTY_FONT;
    private Style cachedFirstStyle = Style.EMPTY.withFont(cachedFont);
    private Style mappedFirstStyle = cachedFirstStyle;

    // For this, pre-initializing this actually makes sense.
    private Style cachedPartStyle = Style.EMPTY;
    private Style mappedPartStyle = cachedPartStyle.withFont(EMPTY_FONT);

    public GlomphoschingFormattedCharSink(final FormattedCharSink sink, final GlomphoscheNode root) {
        this.sink = sink;
        this.root = root;

        this.tail = root;
    }

    private record Capture(int position, Style style, int codepoint) {}

    @Override
    public boolean accept(final int position, final Style style, final int codepoint) {
        final var deque = new ObjectArrayFIFOQueue<Capture>();
        deque.enqueue(new Capture(position, style, codepoint));

        var success = true;
        while (!deque.isEmpty()) {
            final var result = this.catchOrPass(deque.dequeue(), success);
            success = result.success;
            for (final var capture : result.remaining.reversed()) {
                deque.enqueueFirst(capture);
            }
        }

        return this.success = success;
    }

    private record CatchOrPass(ObjectList<Capture> remaining, boolean success) {}

    private CatchOrPass catchOrPass(final Capture capture, final boolean success) {
        final var captures = this.captures;
        final var index = captures.size();
        captures.add(capture);

        final var node = this.tail.get(capture.codepoint);
        if (node.isEmpty()) {
            return this.pass(captures, success);
        } else {
            final var tail = node.get();
            if (tail.getFont().isPresent()) {
                this.candidate = tail.getFont();
                this.index = index;
            }
            this.tail = tail;
            return new CatchOrPass(ObjectList.of(), success);
        }
    }

    @Override
    public void close() {
        final var deque = new ObjectArrayFIFOQueue<Capture>();
        var success = this.success;

        do {
            while (!deque.isEmpty()) {
                final var result = this.catchOrPass(deque.dequeue(), success);
                success = result.success;
                for (final var capture : result.remaining.reversed()) {
                    deque.enqueueFirst(capture);
                }
            }

            final var result = this.pass(this.captures, success);
            for (final var capture : result.remaining) {
                deque.enqueue(capture);
            }
            success = result.success;
        } while (!deque.isEmpty());

        this.success = true;
    }

    private CatchOrPass pass(final ObjectList<Capture> captures, final boolean success) {
        this.tail = this.root;
        this.captures = new ObjectArrayList<>();
        final var result = this.tryBuild(captures, success);
        this.index = 0;
        return new CatchOrPass(
            captures.subList(result.lastIndex + 1, captures.size()),
            result.success
        );
    }

    private record TryBuild(int lastIndex, boolean success) {}

    private TryBuild tryBuild(final ObjectList<Capture> captures, boolean success) {
        final int lastIndex;
        if (captures.isEmpty()) {
            lastIndex = -1;
        } else {
            final var candidate = this.candidate;
            this.candidate = Optional.empty();
            if (candidate.isEmpty()) {
                final var first = captures.getFirst();
                success = success && this.forward(first.position, first.style, first.codepoint);
                lastIndex = 0;
            } else {
                success =
                    this.forwardFirstGlomphosched(captures.getFirst(), candidate.get(), success);
                lastIndex = this.index;
                success = this.forwardPartGlomphosched(captures, lastIndex, success);
            }
        }
        return new TryBuild(lastIndex, success);
    }

    private boolean forwardFirstGlomphosched(
        final Capture first,
        final FontDescription.Resource font,
        final boolean success
    ) {
        final Style mappedStyle;
        if (font == this.cachedFont && first.style == this.cachedFirstStyle) {
            mappedStyle = this.mappedFirstStyle;
        } else {
            this.cachedFont = font;
            this.cachedFirstStyle = first.style;
            mappedStyle = this.mappedFirstStyle = first.style.withFont(font);
        }
        return success && this.forward(first.position, mappedStyle, first.codepoint);
    }

    private boolean forwardPartGlomphosched(
        final ObjectList<Capture> captures,
        final int lastIndex,
        boolean success
    ) {
        var cachedStyle = this.cachedPartStyle;
        var mappedStyle = this.mappedPartStyle;

        for (var i = 1; i <= lastIndex; i++) {
            final var capture = captures.get(i);
            if (capture.style != cachedStyle) {
                cachedStyle = capture.style;
                mappedStyle = capture.style.withFont(EMPTY_FONT);
            }
            success = success && this.forward(capture.position, mappedStyle, capture.codepoint);
        }

        this.cachedPartStyle = cachedStyle;
        this.mappedPartStyle = mappedStyle;

        return success;
    }

    private boolean forward(final int position, final Style style, final int codepoint) {
        return this.sink.accept(position, style, codepoint);
    }
}
