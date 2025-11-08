package io.github.startsmercury.glomphosche.impl.client.font;

import net.minecraft.network.chat.FontDescription;

/**
 * Non-standard font description hinting at invisible glyphs.
 * <p>
 * Internally used to retain the codepoint data for use by
 * {@code FormattedCharSink}s while still keeping them invisible; the first
 * codepoint is the only one visible, the rest are invisible.
 */
public record EmptyFont() implements FontDescription {
    private static final EmptyFont INSTANCE = new EmptyFont();

    public static EmptyFont instance() {
        return INSTANCE;
    }
}
