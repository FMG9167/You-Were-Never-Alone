package io.github.FMG9167.ywna.tracking;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import io.github.FMG9167.ywna.profile.ProfileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BehaviorTracker {

    private static final int SAMPLE_INTERVAL = 20;
    private static final int ROLLING_WINDOW = 200;
    private static final int DWELL_THRESHOLD_TICKS = 200;
    private static final float LOOK_BEHIND_THRESHOLD = 150.0f;
    private static final float CAMERA_VARIANCE_MAX_DELTA = 60f;
    private static final int SEARCH_RADIUS = 16;

    private float accSpeed = 0;
    private int accSprint = 0;
    private int accSneak = 0;
    private float accYawDelta = 0;
    private int accumTicks = 0;

    private final float[] recentYaw = new float[10];
    private int yawCursor = 0;

    private BlockPos dwellAnchor = null;
    private int dwellTicks = 0;

    private Vec3d prevPos = null;
    private float prevYaw = Float.NaN;

    public static void tick(ServerPlayerEntity player) {
        PlayerProfile profile = ProfileManager.get(player);
        profile.totalObservedTicks++;
        BehaviorTracker tracker = getTracker(profile);
        tracker.accumulate(player, profile);
        if(profile.totalObservedTicks % SAMPLE_INTERVAL == 0) {
            tracker.flush(player, profile);
        }
    }

    private void accumulate(ServerPlayerEntity player, PlayerProfile profile) {
        Vec3d pos = player.getPos();
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        if (prevPos != null) {
            double dx = pos.x - prevPos.x;
            double dz = pos.z - prevPos.z;
            accSpeed += (float) Math.sqrt(dx * dx + dz * dz);
        }
        prevPos = pos;

        if(player.isSprinting()) {accSprint++;}
        if(player.isSneaking()) {accSneak++;}

        if(!Float.isNaN(prevYaw)) {
            float rawDelta = Math.abs(yaw - prevYaw);
            if(rawDelta > 180f) rawDelta = 360f - rawDelta;
            if(rawDelta < CAMERA_VARIANCE_MAX_DELTA) accYawDelta += rawDelta;
        }
        prevYaw = yaw;

        recentYaw[yawCursor % recentYaw.length] = yaw;
        yawCursor++;
        if(yawCursor >= recentYaw.length) {
            float oldest =  recentYaw[yawCursor % recentYaw.length];
            float totalDelta = Math.abs(yaw - oldest);
            if(totalDelta > 180f) totalDelta = 360f - totalDelta;
            if(totalDelta > LOOK_BEHIND_THRESHOLD) {
                profile.lookBehindCount++;
            }
        }

        if(profile.lookHistory.size() >= PlayerProfile.LOOK_HISTORY_SIZE) {
            profile.lookHistory.pollFirst();
        }
        profile.lookHistory.addLast(new float[] {yaw, pitch});

        profile.hotbarUsageCount[player.getInventory().selectedSlot]++;

        BlockPos currentBlock = player.getBlockPos();
        if(dwellAnchor == null) {
            dwellAnchor = currentBlock;
            dwellTicks = 0;
        } else if(currentBlock.isWithinDistance(dwellAnchor, 8.0)) {
            dwellTicks++;
            if (dwellTicks >= DWELL_THRESHOLD_TICKS) {
                updateSafeZone(player.getServerWorld(), profile, pos);
            }
        } else {
            dwellAnchor = currentBlock;
            dwellTicks = 0;
        }

        accumTicks++;
    }

    private void flush(ServerPlayerEntity player, PlayerProfile profile) {
        if(accumTicks == 0) return;

        float w = 1.0f / ROLLING_WINDOW;
        profile.avgMoveSpeed = lerp(profile.avgMoveSpeed, accSpeed / accumTicks, w);
        profile.sprintFrac = lerp(profile.sprintFrac, (float) accSprint / accumTicks, w);
        profile.sneakFrac = lerp(profile.sneakFrac, (float) accSneak / accumTicks, w);
        profile.cameraVariance = lerp(profile.cameraVariance, accYawDelta / accumTicks, w);
        snapshotHotbar(player, profile);
        accSpeed = 0; accSprint = 0;  accSneak = 0; accYawDelta = 0; accumTicks = 0;
    }

    private void updateSafeZone(ServerWorld world, PlayerProfile profile, Vec3d pos) {
        profile.safeZoneSumX += pos.x;
        profile.safeZoneSumZ += pos.z;
        profile.safeZoneSamples++;
        profile.safeZone = new BlockPos(
                (int)(profile.safeZoneSumX / profile.safeZoneSamples),
                dwellAnchor.getY(),
                (int)(profile.safeZoneSumZ / profile.safeZoneSamples)
        );

        List<BlockPos> doors = new ArrayList<>();

        BlockPos.iterate(
                profile.safeZone.add(-SEARCH_RADIUS, -SEARCH_RADIUS/2, -SEARCH_RADIUS),
                profile.safeZone.add(SEARCH_RADIUS, SEARCH_RADIUS/2, SEARCH_RADIUS)
        ).forEach(p -> {
            BlockState state = world.getBlockState(p);
            if(state.getBlock() instanceof DoorBlock && state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                doors.add(p.toImmutable());
            }
        });

        if(doors.isEmpty()) {
            YWNAMod.LOGGER.info("[YWNA] BehaviorTracker: No doors found near safe zone");
            return;
        }

        doors.sort(Comparator.comparingDouble(p -> p.getSquaredDistance(profile.safeZone)));
        profile.safeZoneDoor = doors.get(0);
    }

    private void snapshotHotbar(ServerPlayerEntity player, PlayerProfile profile) {
        for (int i = 0; i < 9; i++) {
            var stack = player.getInventory().getStack(i);
            profile.hotbarSnapshot[i] = stack.isEmpty() ? "air" : Registries.ITEM.getId(stack.getItem()).toString();
        }
    }

    private static final Map<UUID, BehaviorTracker> TRACKERS = new HashMap<>();

    private static BehaviorTracker getTracker(PlayerProfile profile) {
        return TRACKERS.computeIfAbsent(profile.playerUUID, uuid -> new BehaviorTracker());
    }

    public static void unload(ServerPlayerEntity player) {
        TRACKERS.remove(player.getUuid());
    }

    private static float lerp(float current, float target, float weight) {
        return current + weight * (target - current);
    }
}
