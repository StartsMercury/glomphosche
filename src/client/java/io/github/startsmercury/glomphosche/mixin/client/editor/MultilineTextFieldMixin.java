package io.github.startsmercury.glomphosche.mixin.client.editor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.startsmercury.glomphosche.impl.client.TextTraverser;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldMixin {
    @Unique
    private final TextTraverser glomphosche$traverser = new TextTraverser();

    @Shadow
    private String value;

    @Inject(method = "onValueChange", at = @At("HEAD"))
    private void glomphosche$invalidatePositions(final CallbackInfo callback) {
        this.glomphosche$traverser.invalidatePositions(this.value);
    }

    @WrapOperation(
        method = "seekCursor",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/components/MultilineTextField;cursor:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void glomphosche$offsetCursorByGraphemesOnMove(
        final MultilineTextField instance,
        final int cursor,
        final Operation<Void> original,
        final @Local(ordinal = 0, argsOnly = true) Whence whence,
        final @Local(ordinal = 0, argsOnly = true) int dir
    ) {
        if (whence != Whence.RELATIVE) {
            original.call(instance, dir);
        }

        original.call(
            instance,
            this.glomphosche$traverser.offsetByGraphemes(this.value.length(), cursor, dir)
        );
    }

    @ModifyExpressionValue(
        method = "deleteText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(III)I"
        )
    )
    private int glomphosche$removeGraphemeFromCursor(
        final int cursor,
        final @Local(ordinal = 0, argsOnly = true) int dir
    ) {
        if (dir <= 0) {
            return cursor;
        }
        return this.glomphosche$traverser.offsetByGraphemes(this.value.length(), cursor, dir);
    }
}
