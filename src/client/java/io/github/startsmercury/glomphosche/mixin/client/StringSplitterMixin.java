package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.GlomphoschingFormattedCharSink;
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
        try (final var glomphoscheSink = new GlomphoschingFormattedCharSink(formattedCharSink, GlomphoscheImpl.ROOT)) {
            return original.call(instance, glomphoscheSink);
        }
    }
}
