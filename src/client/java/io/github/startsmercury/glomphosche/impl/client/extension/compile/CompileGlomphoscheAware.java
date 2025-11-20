package io.github.startsmercury.glomphosche.impl.client.extension.compile;

import io.github.startsmercury.glomphosche.impl.client.GlomphoscheImpl;
import io.github.startsmercury.glomphosche.impl.client.extension.GlomphoscheAware;

@Deprecated
@SuppressWarnings("DeprecatedIsStillUsed")
public interface CompileGlomphoscheAware extends GlomphoscheAware {
    @Override
    default GlomphoscheImpl getGlomphosche() {
        return CompileAwareHelper.unimplemented();
    }
}
