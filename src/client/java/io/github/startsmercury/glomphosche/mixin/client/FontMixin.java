package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.startsmercury.glomphosche.impl.client.font.EmptyFont;
import io.github.startsmercury.glomphosche.impl.client.font.ReplaceGlyphFont;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Font.class)
public class FontMixin {
    @WrapMethod(
        method = "getGlyph(ILnet/minecraft/network/chat/Style;)Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;"
    )
    private BakedGlyph glomphosche$enforceEmptyFont(
        final int codepoint,
        final Style style,
        final Operation<BakedGlyph> original
    ) {
        // Note: We trust that the implementation remains to rely only on
        //       `style.font` and `codepoint`.
        return switch (style.getFont()) {
            case EmptyFont() -> original.call(0x200c, Style.EMPTY);
            case ReplaceGlyphFont(final var replacement, final var font) ->
                original.call(replacement, Style.EMPTY.withFont(font));
            default -> original.call(codepoint, style);
        };
    }

    @WrapMethod(method = "method_27516")
    private float glomphosche$enforceEmptyFontOnSplitter(
        final int codepoint,
        final Style style,
        final Operation<Float> original
    ) {
        // Note: We trust that the implementation remains to rely only on
        //       `style.font` and `codepoint`.
        return switch (style.getFont()) {
            case EmptyFont() -> original.call(0x200c, Style.EMPTY);
            case ReplaceGlyphFont(final var replacement, final var font) ->
                original.call(replacement, Style.EMPTY.withFont(font));
            default -> original.call(codepoint, style);
        };
    }
}
