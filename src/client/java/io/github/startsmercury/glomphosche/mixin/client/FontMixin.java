package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
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
        if (GlomphoscheImpl.EMPTY_FONT.equals(style.getFont())) {
            return original.call(0x200c, Style.EMPTY);
        } else {
            return original.call(codepoint, style);
        }
    }

    @WrapMethod(method = "method_27516")
    private float glomphosche$enforceEmptyFontOnSplitter(
        final int codepoint,
        final Style style,
        final Operation<Float> original
    ) {
        if (GlomphoscheImpl.EMPTY_FONT.equals(style.getFont())) {
            return original.call(0x200c, Style.EMPTY);
        } else {
            return original.call(codepoint, style);
        }
    }
}
