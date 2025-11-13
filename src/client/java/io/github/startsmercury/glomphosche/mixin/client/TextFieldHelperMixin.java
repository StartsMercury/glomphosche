package io.github.startsmercury.glomphosche.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.startsmercury.glomphosche.impl.client.TextTraverser;
import io.github.startsmercury.glomphosche.impl.client.extension.TextTraverserAware;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.font.TextFieldHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldHelper.class)
public abstract class TextFieldHelperMixin implements TextTraverserAware {
    @Unique
    private final TextTraverser glomphosche$traverser = new TextTraverser();

    @Final
    @Shadow
    private Supplier<String> getMessageFn;

    @Final
    @Mutable
    @Shadow
    private Consumer<String> setMessageFn;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void glomphosche$injectPositionsUpdater(final CallbackInfo callback) {
        this.setMessageFn = this.setMessageFn.andThen(this::glomphosche$invalidateTraverser);
        this.glomphosche$invalidateTraverser();
    }

    @Override
    public TextTraverser glomphosche$getTraverser() {
        return this.glomphosche$traverser;
    }

    @Override
    public String glomphosche$getValue() {
        return this.getMessageFn.get();
    }

    @WrapOperation(
        method = "moveByChars(IZ)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/font/TextFieldHelper;cursorPos:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void glomphosche$offsetCursorByGraphemesOnMove(
        final TextFieldHelper instance,
        final int cursor,
        final Operation<Void> original,
        final @Local(ordinal = 0, argsOnly = true) int dir
    ) {
        original.call(
            instance,
            this.glomphosche$traverser
                .offsetByGraphemes(this.getMessageFn.get().length(), cursor, dir)
        );
    }

    @WrapOperation(
        method = "removeCharsFromCursor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/Util;offsetByCodepoints(Ljava/lang/String;II)I"
        )
    )
    private int glomphosche$removeGraphemeFromCursor(
        final String string,
        final int cursor,
        final int dir,
        final Operation<Integer> original
    ) {
        final var result = original.call(string, cursor, dir);
        if (dir <= 0) {
            return result;
        }
        return this.glomphosche$traverser.offsetByGraphemes(string.length(), result, dir);
    }
}
