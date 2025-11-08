package io.github.startsmercury.glomphosche.impl.client;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

public record FormattedCharPosSink(IntList positions) implements FormattedCharSink {
    public FormattedCharPosSink() {
        this(new IntArrayList());
    }

    @Override
    public boolean accept(final int position, final Style style, final int codepoint) {
        if (codepoint != '\u200c' && !GlomphoscheImpl.EMPTY_FONT.equals(style.getFont())) {
            this.positions.add(position);
        }
        return true;
    }
}
