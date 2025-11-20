package io.github.startsmercury.glomphosche.mixin.client.editor;

import io.github.startsmercury.glomphosche.impl.client.extension.TextTraverserAware;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {
    @Shadow
    @Nullable
    private TextFieldHelper signField;

    @Inject(
        method = "keyPressed",
        at = @At(
            value = "FIELD",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractSignEditScreen;line:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void glomphosche$updateTraverseOnSwitchLinesOnKeyPress(
        final CallbackInfoReturnable<Boolean> callback
    ) {
        final var signField = this.signField;
        if (signField == null) return;
        assert signField instanceof TextTraverserAware;
        ((TextTraverserAware) signField).glomphosche$invalidateTraverser();
    }
}
