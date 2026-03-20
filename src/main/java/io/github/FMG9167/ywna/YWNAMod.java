package io.github.FMG9167.ywna;

import io.github.FMG9167.ywna.phase.PhaseManager;
import io.github.FMG9167.ywna.profile.ProfileManager;
import io.github.FMG9167.ywna.tracking.BehaviorTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class YWNAMod implements ModInitializer {

    public static final String MOD_ID = "ywna";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[YWNA] Initializing...");

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            PhaseManager.tick(server);
            for(var player : server.getPlayerManager().getPlayerList()) {
                BehaviorTracker.tick(player);
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, livingEntity) -> {
            if(entity instanceof ServerPlayerEntity player) {
                var profile = ProfileManager.get(player);
                profile.lastDeathPos = player.getBlockPos();
                LOGGER.info("[YWNA] Player {} died at {} ", player, player.getBlockPos().toString());
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.player;
            var profile = ProfileManager.get(player);
            profile.sessionStartHour = LocalTime.now().getHour();
            profile.sessionStartTick = server.getTicks();
            LOGGER.info("[YWNA] {} joined. fearScore={}, phase={}, observedTicks={}",
                    player.getName().toString(),
                    profile.fearScore,
                    profile.getPhase(),
                    profile.totalObservedTicks);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.player;
            var profile = ProfileManager.get(player);
            var sessionLength = server.getTicks() - profile.sessionStartTick;
            profile.totalSessions++;
            profile.avgSessionLengthTicks += (sessionLength - profile.avgSessionLengthTicks) / profile.totalSessions;
            ProfileManager.getInstance().markDisconnecting(player);
        });

        LOGGER.info("[YWNA] Done.");
    }
}
