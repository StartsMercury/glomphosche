package io.github.startsmercury.glomphosche.mixin.client.glyph;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StringSplitter.class)
public class StringSplitterMixin {
    @WrapOperation(
        method = "stringWidth(Lnet/minecraft/util/FormattedCharSequence;)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/FormattedCharSequence;accept(Lnet/minecraft/util/FormattedCharSink;)Z"
        )
    )
    private boolean glomphosche$wrapStringWidth(
        final FormattedCharSequence instance,
        final FormattedCharSink formattedCharSink,
        final Operation<Boolean> original
    ) {
        try (final var glomphoscheSink = Minecraft.getInstance().getGlomphosche().forSink(formattedCharSink)) {
            return original.call(instance, glomphoscheSink);
        }
    }
}
