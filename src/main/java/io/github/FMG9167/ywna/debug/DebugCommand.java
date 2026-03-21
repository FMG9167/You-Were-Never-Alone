package io.github.FMG9167.ywna.debug;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import io.github.FMG9167.ywna.networking.YWNAPackets;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import io.github.FMG9167.ywna.profile.ProfileManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

public class DebugCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        dispatcher.register(CommandManager.literal("ywna")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.literal("fear")
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg(0, 1))
                                                .executes(ctx -> {
                                                    ServerCommandSource source = ctx.getSource();
                                                    ServerPlayerEntity player = source.getPlayer();
                                                    if (player == null) return 0;
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    PlayerProfile profile = ProfileManager.get(player);
                                                    profile.fearScore = value;
                                                    source.sendFeedback(() ->
                                                            Text.literal("[YWNA] Fear Score set to " + value + " (phase " + profile.getPhase() + ")"), false);
                                                    return 1;
                                                })
                                        )
                                )

                                .then(CommandManager.literal("ticks")
                                        .then(CommandManager.argument("value", LongArgumentType.longArg(0))
                                                .executes(ctx -> {
                                                    ServerCommandSource source = ctx.getSource();
                                                    ServerPlayerEntity player = source.getPlayer();
                                                    if (player == null) return 0;
                                                    long value = LongArgumentType.getLong(ctx, "value");
                                                    PlayerProfile profile = ProfileManager.get(player);
                                                    profile.totalObservedTicks = value;
                                                    source.sendFeedback(() ->
                                                            Text.literal("[YWNA] Observed Ticks set to " + value), false);
                                                    return 1;
                                                })
                                        )
                                )

                                .then(CommandManager.literal("status")
                                        .executes(ctx -> {
                                            ServerCommandSource source = ctx.getSource();
                                            ServerPlayerEntity player = source.getPlayer();
                                            if (player == null) return 0;
                                            PlayerProfile profile = ProfileManager.get(player);
                                            source.sendFeedback(() -> Text.literal(
                                                      "[YWNA] fearScore="          + profile.fearScore +
                                                            " phase="                    + profile.getPhase() +
                                                            " ticks="                    + profile.totalObservedTicks +
                                                            " lookBehind="               + profile.lookBehindCount +
                                                            " safeZone="                 + profile.safeZone +
                                                            " safeZoneDoor="             + profile.safeZoneDoor +
                                                            " lastDeathPos="             + profile.lastDeathPos +
                                                            " avgSessionLength="         + profile.avgSessionLengthTicks +
                                                            " totalSessions="            + profile.totalSessions +
                                                            " hotbarUsageCount="         + Arrays.toString(profile.hotbarUsageCount)
                                            ), false);
                                            return 1;
                                        })
                                )

                                .then(CommandManager.literal("packet")
                                        .then(CommandManager.literal("test")
                                                .executes(ctx -> {
                                                    ServerCommandSource source = ctx.getSource();
                                                    ServerPlayerEntity player = source.getPlayer();
                                                    if (player == null) return 0;
                                                    YWNAPackets.sendTest(player);
                                                    source.sendFeedback(() -> Text.literal(
                                                            "[YWNA] Test packet sent."
                                                    ), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}
