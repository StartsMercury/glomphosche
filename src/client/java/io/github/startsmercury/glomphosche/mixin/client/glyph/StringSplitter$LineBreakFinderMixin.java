package io.github.startsmercury.glomphosche.mixin.client.glyph;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.startsmercury.glomphosche.impl.client.font.ReplaceGlyphFont;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/StringSplitter$LineBreakFinder")
public class StringSplitter$LineBreakFinderMixin {
    @Inject(method = "finishIteration", at = @At("HEAD"))
    private void glomphosche$(
        final CallbackInfoReturnable<Boolean> callback,
        final @Local(ordinal = 0, argsOnly = true) LocalRef<Style> style
    ) {
        final var s = style.get();
        if (s.getFont() instanceof final ReplaceGlyphFont font) {
            style.set(s.withFont(font.original()));
        }
    }
}
