package io.github.startsmercury.glomphosche.impl.client.font;

import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;

/**
 * Internal intermediate font description hinting glyph visual replacement.
 * <p>
 * This is a non-standard font description that hints to request a glyph texture
 * for the codepoint override instead of the original one.
 * <p>
 * This font description also stores another font description since it will
 * replace the pre-existing value in {@code Style.font}.
 * @param codepoint  The codepoint override.
 * @param font  The replacement font though this often is the original font.
 * @see Style#getFont()
 */
public record ReplaceGlyphFont(int codepoint, FontDescription font) implements FontDescription {
}
