package io.github.startsmercury.glomphosche.impl.client;

import io.github.startsmercury.glomphosche.impl.client.font.EmptyFont;
import io.github.startsmercury.glomphosche.impl.client.font.ReplaceGlyphFont;
import io.github.startsmercury.glomphosche.impl.client.node.Node;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Optional;
import java.util.OptionalInt;
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
    private final Node root;

    private ObjectList<Capture> captures = new ObjectArrayList<>();
    private Optional<Node> candidate = Optional.empty();
    private boolean success = true;
    private Node tail;
    private int index;

    // Use unused font as initial cache mapping. Cant predict the mapped font.
    private FontDescription cachedFont = EmptyFont.instance();
    private Style cachedFirstStyle = Style.EMPTY.withFont(cachedFont);
    private Style mappedFirstStyle = cachedFirstStyle;

    // For this, pre-initializing this actually makes sense.
    private Style cachedPartStyle = Style.EMPTY;
    private Style mappedPartStyle = cachedPartStyle.withFont(EmptyFont.instance());

    public GlomphoschingFormattedCharSink(final FormattedCharSink sink, final Node root) {
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

        final var node = this.tail.visit(capture.codepoint);
        if (node.isEmpty()) {
            return this.pass(captures, success);
        } else {
            final var tail = node.get();
            if (tail.hasOverrides()) {
                this.candidate = node;
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
    }

    public boolean pop() {
        final var success = this.success;
        this.success = true;
        return success;
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
        final Node node,
        final boolean success
    ) {
        final Style mappedStyle;
        if (node.getCodepointOverride().isPresent()) {
            mappedStyle = first.style.withFont(new ReplaceGlyphFont(
                node.getCodepointOverride(), // This is not empty
                node.getFontOverride(),
                first.style.getFont()
            ));
        } else {
            // Since this node was a candidate, either codepoint or font
            // override must be present, and since we eliminated the latter,
            // then it must be that the former is present.
            assert node.getFontOverride().isPresent();
            final var font = node.getFontOverride().get();
            if (font == this.cachedFont && first.style == this.cachedFirstStyle) {
                mappedStyle = this.mappedFirstStyle;
            } else {
                this.cachedFont = font;
                this.cachedFirstStyle = first.style;
                mappedStyle = this.mappedFirstStyle = first.style.withFont(
                    new ReplaceGlyphFont(
                        OptionalInt.empty(),
                        Optional.of(font),
                        first.style.getFont()
                    )
                );
            }
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
                mappedStyle = capture.style.withFont(EmptyFont.instance());
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
