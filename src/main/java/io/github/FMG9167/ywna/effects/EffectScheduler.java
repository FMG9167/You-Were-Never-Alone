package io.github.FMG9167.ywna.effects;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

public class EffectScheduler {
    private static final Random RANDOM = new Random();

    private static final long MIN_DELAY_TICKS = 20L;
    private static final long MAX_DELAY_TICKS = 200L;

    public static void schedule(ServerPlayerEntity player, PlayerProfile profile, EffectType effect, long currentTick) {
        long delay = MIN_DELAY_TICKS + (long)(RANDOM.nextFloat() * (MAX_DELAY_TICKS - MIN_DELAY_TICKS));
        long fireAt = currentTick + delay;

        profile.markEffectFired(effect.ordinalIndex, fireAt);

        YWNAMod.LOGGER.info("[YWNA] EffectScheduler: Scheduled {} for {} at tick {}", effect, player.getName().toString(), fireAt);

        fire(player, profile, effect, currentTick);
    }

    private static void fire(ServerPlayerEntity player, PlayerProfile profile, EffectType effect, long currentTick) {
        switch(effect) {
            case MISSING_ITEM         -> MissingItemEffect.fire(player, profile, currentTick);
            case OPENED_DOOR          -> OpenedDoorEffect.fire(player, profile);
            case CHAT_CORRUPTION      -> ChatCorruptionEvent.fire(player, profile);
            case CAMERA_DRIFT         -> YWNAMod.LOGGER.info("[YWNA] CameraDrift");
            case UI_FLICKER           -> YWNAMod.LOGGER.info("[YWNA] UIFlicker");
            case INPUT_HESITATION     -> YWNAMod.LOGGER.info("[YWNA] InputHesitation");
            case DOPPELGANGER_GLIMPSE -> YWNAMod.LOGGER.info("[YWNA] DoppelgangerGlimpse");
        }
    }
}
