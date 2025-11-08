package io.github.startsmercury.glomphosche.impl.client.node;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.network.chat.FontDescription;

public interface DefaultNode extends Node {
    @Override
    default OptionalInt getCodepointOverride() {
        return OptionalInt.empty();
    }

    @Override
    default Optional<FontDescription> getFontOverride() {
        return Optional.empty();
    }
}
