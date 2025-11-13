package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.startsmercury.glomphosche.impl.client.TextTraverser;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Unique
    private final TextTraverser glomphosche$traverser = new TextTraverser();

    @Shadow
    private String value;

    @Inject(method = "onValueChange", at = @At("HEAD"))
    private void glomphosche$invalidateTraverser(final String value, final CallbackInfo callback) {
        this.glomphosche$traverser.invalidatePositions(value);
    }

    @WrapOperation(
        method = "moveCursor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;getCursorPos(I)I"
        )
    )
    private int glomphosche$offsetCursorByGraphemesOnMove(
        final EditBox instance,
        final int dir,
        final Operation<Integer> original
    ) {
        final var cursor = original.call(instance, dir);
        return glomphosche$traverser.offsetByGraphemes(this.value.length(), cursor, dir);
    }

    @WrapOperation(
        method = "deleteChars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;getCursorPos(I)I"
        )
    )
    private int glomphosche$removeGraphemeFromCursor(
        final EditBox instance,
        final int dir,
        final Operation<Integer> original
    ) {
        final var result = original.call(instance, dir);
        if (dir <= 0) {
            return result;
        }
        return this.glomphosche$traverser.offsetByGraphemes(this.value.length(), result, dir);
    }
}
