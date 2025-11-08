package io.github.startsmercury.glomphosche.impl.client.node;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.network.chat.FontDescription;

public interface Node {
    Optional<Node> visit(int codepoint);

    OptionalInt getCodepointOverride();

    Optional<FontDescription> getFontOverride();

    default boolean hasOverrides() {
        return this.getCodepointOverride().isPresent() || this.getFontOverride().isPresent();
    }
}
