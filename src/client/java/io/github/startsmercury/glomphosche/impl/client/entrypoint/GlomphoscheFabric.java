package io.github.startsmercury.glomphosche.impl.client.entrypoint;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class GlomphoscheFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        final var glomphosche = Minecraft.getInstance().getGlomphosche();
        glomphosche.initialize();
    }
}
