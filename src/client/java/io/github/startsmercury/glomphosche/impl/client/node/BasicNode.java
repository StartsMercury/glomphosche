package io.github.startsmercury.glomphosche.impl.client.node;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.network.chat.FontDescription;

public class BasicNode implements Node {
    private OptionalInt codepointOverride = OptionalInt.empty();

    private Optional<FontDescription> fontOverride = Optional.empty();

    @Override
    public Optional<Node> visit(final int codepoint) {
        return Optional.empty();
    }

    @Override
    public OptionalInt getCodepointOverride() {
        return this.codepointOverride;
    }

    public void setCodepointOverride(final OptionalInt codepointOverride) {
        this.codepointOverride = codepointOverride;
    }

    public BasicNode codepointOverride(final int codepointOverride) {
        this.setCodepointOverride(OptionalInt.of(codepointOverride));
        return this;
    }

    public BasicNode codepointOverride() {
        this.setCodepointOverride(OptionalInt.empty());
        return this;
    }

    @Override
    public Optional<FontDescription> getFontOverride() {
        return this.fontOverride;
    }

    public void setFontOverride(final Optional<FontDescription> fontOverride) {
        this.fontOverride = fontOverride;
    }

    public BasicNode fontOverride(final FontDescription fontOverride) {
        this.setFontOverride(Optional.of(fontOverride));
        return this;
    }

    public BasicNode fontOverride() {
        this.setFontOverride(Optional.empty());
        return this;
    }
}
