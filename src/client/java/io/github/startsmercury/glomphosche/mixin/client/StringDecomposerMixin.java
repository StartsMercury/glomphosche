package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.GlomphoschingFormattedCharSink;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StringDecomposer.class)
public class StringDecomposerMixin {
    @WrapMethod(method = "iterate")
    private static boolean glomphosche$wrapIterate(
        final String string,
        final Style style,
        final FormattedCharSink formattedCharSink,
        final Operation<Boolean> original
    ) {
        try (final var glomphoscheSink = new GlomphoschingFormattedCharSink(formattedCharSink, GlomphoscheImpl.ROOT)) {
            return original.call(string, style, glomphoscheSink);
        }
    }

    @WrapMethod(method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z")
    private static boolean glomphosche$wrapIterateFormatted(
        final String string,
        final int i,
        final Style style,
        final Style style2,
        final FormattedCharSink formattedCharSink,
        final Operation<Boolean> original
    ) {
        try (final var glomphoscheSink = new GlomphoschingFormattedCharSink(formattedCharSink, GlomphoscheImpl.ROOT)) {
            return original.call(string, i, style, style2, glomphoscheSink);
        }
    }
}
