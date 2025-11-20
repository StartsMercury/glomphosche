package io.github.startsmercury.glomphosche.mixin.client.extension;

import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.extension.compile.CompileGlomphoscheAware;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
@SuppressWarnings("deprecation")
public class MinecraftMixin implements CompileGlomphoscheAware {
    @Unique
    private final GlomphoscheImpl glomphosche = new GlomphoscheImpl((Minecraft) (Object) this);

    @Override
    public GlomphoscheImpl getGlomphosche() {
        return this.glomphosche;
    }
}
