package io.github.FMG9167.ywna.phase;

import io.github.FMG9167.ywna.effects.EffectScheduler;
import io.github.FMG9167.ywna.effects.EffectType;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import io.github.FMG9167.ywna.profile.ProfileManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhaseManager {
    private static final Random RANDOM = new Random();

    private static final float FEAR_PER_NOTICED_EFFECT = 0.05f;
    private static final float FEAR_PER_LOOK_BEHIND = 0.001f;
    private static final int LOOK_BEHIND_FEAR_INTERVAL = 200;

    public static void tick(MinecraftServer server) {
        long currentTick = server.getOverworld().getTime();
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerProfile profile = ProfileManager.get(player);
            tickPlayer(player, profile, currentTick);
        }
    }

    private static void tickPlayer(ServerPlayerEntity player, PlayerProfile profile, long currentTick) {
        raiseFearFromNoticed(profile);
        raiseFearFromLookBehind(profile, currentTick);
    }

    private static void raiseFearFromNoticed(PlayerProfile profile) {
    }

    private static void raiseFearFromLookBehind(PlayerProfile profile, long currentTick) {
        if(currentTick % LOOK_BEHIND_FEAR_INTERVAL == 0) {
            if(profile.lookBehindCount > 0) {
                profile.raiseFear(FEAR_PER_LOOK_BEHIND * profile.lookBehindCount);
            }
        }
    }
}
