package io.github.startsmercury.glomphosche.impl.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.network.chat.FontDescription;

public class GlomphoscheNode {
    private final Int2ObjectOpenHashMap<GlomphoscheNode> branches = new Int2ObjectOpenHashMap<>();

    private Optional<FontDescription.Resource> font = Optional.empty();

    public Optional<GlomphoscheNode> get(final int codepoint) {
        return Optional.ofNullable(this.branches.get(codepoint));
    }

    public GlomphoscheNode getOrCreate(final int codepoint) {
        return this.branches.computeIfAbsent(codepoint, k -> new GlomphoscheNode());
    }

    public Optional<FontDescription.Resource> setFont(final Optional<FontDescription.Resource> font) {
        final var previous = this.font;
        this.font = font;
        return previous;
    }

    public Optional<FontDescription.Resource> register(final FontDescription.Resource font) {
        return this.setFont(Optional.of(font));
    }

    public Optional<FontDescription.Resource> unregister() {
        return this.setFont(Optional.empty());
    }

    public Optional<FontDescription.Resource> getFont() {
        return this.font;
    }
}
