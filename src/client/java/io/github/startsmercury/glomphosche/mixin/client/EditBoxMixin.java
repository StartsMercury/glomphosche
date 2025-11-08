package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.startsmercury.glomphosche.impl.client.FormattedCharPosSink;
import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.GlomphoschingFormattedCharSink;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Shadow
    private String value;

    @Unique
    private CompletableFuture<int[]> positions = CompletableFuture.completedFuture(new int[] { 0 });

    @Inject(method = "onValueChange", at = @At("HEAD"))
    private void glomphosche$updatePositions(final String value, final CallbackInfo callback) {
        this.positions = CompletableFuture.supplyAsync(() -> {
            final var posSink = new FormattedCharPosSink();
            try (final var glomphoscheSink = new GlomphoschingFormattedCharSink(posSink, GlomphoscheImpl.ROOT)) {
                StringDecomposer.iterate(value, Style.EMPTY, glomphoscheSink);
            }
            return posSink.positions().toIntArray();
        });
    }

    @WrapOperation(
        method = "moveCursor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;getCursorPos(I)I"
        )
    )
    private int glomphosche$correctCursorOnMove(
        final EditBox instance,
        final int i,
        final Operation<Integer> original
    ) {
        final var cursor = original.call(instance, i);
        return GlomphoscheImpl.moveCursorSkipZeroWidths(this.positions, value, i, cursor);
    }
}
