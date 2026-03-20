package io.github.FMG9167.ywna.effects;

public enum EffectType {

    CAMERA_DRIFT            (0, 1, 1200, true),
    MISSING_ITEM            (1, 1, 6000, false),
    UI_FLICKER              (2, 1, 2400, true),
    OPENED_DOOR             (3, 2, 4800, false),
    CHAT_CORRUPTION         (4, 2, 3600, false),
    DOPPELGANGER_GLIMPSE    (5, 2, 7200, false),
    INPUT_HESITATION        (6, 3, 4800, true);

    public final int ordinalIndex;
    public final int minPhase;
    public final long cooldownTicks;
    public final boolean clientSide;

    EffectType(int ordinalIndex, int minPhase, long cooldownTicks, boolean clientSide) {
        this.ordinalIndex = ordinalIndex;
        this.minPhase = minPhase;
        this.cooldownTicks = cooldownTicks;
        this.clientSide = clientSide;
    }
}
