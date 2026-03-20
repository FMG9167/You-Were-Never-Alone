package io.github.FMG9167.ywna;

import io.github.FMG9167.ywna.phase.PhaseManager;
import io.github.FMG9167.ywna.profile.ProfileManager;
import io.github.FMG9167.ywna.tracking.BehaviorTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.player;
            var profile = ProfileManager.get(player);
            LOGGER.info("[YWNA] {} joined. fearScore={}, phase={}, observedTicks={}",
                    player.getName().toString(),
                    profile.fearScore,
                    profile.getPhase(),
                    profile.totalObservedTicks);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, sender) -> {
            var player = handler.player;
            ProfileManager.getInstance().markDisconnecting(player);
        });

        LOGGER.info("[YWNA] Done.");
    }
}
