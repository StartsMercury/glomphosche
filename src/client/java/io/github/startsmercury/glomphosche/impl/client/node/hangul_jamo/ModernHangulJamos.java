package io.github.startsmercury.glomphosche.impl.client.node.hangul_jamo;

import org.jetbrains.annotations.Range;

public final class ModernHangulJamos {
    public static int composeFromIndices(
        final @Range(from = 0, to = ChoseongNode.COUNT - 1) int choseong,
        final @Range(from = 0, to = JungseongNode.COUNT - 1) int jungseong,
        final @Range(from = 0, to = JongseongNode.COUNT) int jongseong
    ) {
        final var HANGUL_SYLLABLES_START = 0XAC00;
        return HANGUL_SYLLABLES_START
           + ((choseong * JungseongNode.COUNT) + jungseong) * (1 + JongseongNode.COUNT) + jongseong;
    }

    private ModernHangulJamos() {}
}
