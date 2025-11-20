package io.github.startsmercury.glomphosche.mixin.client.glyph;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.startsmercury.glomphosche.impl.client.font.EmptyFont;
import io.github.startsmercury.glomphosche.impl.client.font.ReplaceGlyphFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Font.class)
public class FontMixin {
    @WrapOperation(
        method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FormattedCharSequence;accept(Lnet/minecraft/util/FormattedCharSink;)Z")
    )
    private boolean glomphosche$wrapPrepareText(
        final FormattedCharSequence instance,
        final FormattedCharSink formattedCharSink,
        final Operation<Boolean> original
    ) {
        try (final var glomphoscheSink = Minecraft.getInstance().getGlomphosche().forSink(formattedCharSink)) {
            return original.call(instance, glomphoscheSink);
        }
    }

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
            case ReplaceGlyphFont(final var replacement, final var font, final var ignored) ->
                original.call(
                    replacement.orElse(codepoint),
                    font.map(Style.EMPTY::withFont).orElse(Style.EMPTY)
                );
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
            case ReplaceGlyphFont(final var replacement, final var font, final var ignored) ->
                original.call(
                    replacement.orElse(codepoint),
                    font.map(Style.EMPTY::withFont).orElse(Style.EMPTY)
                );
            default -> original.call(codepoint, style);
        };
    }
}
