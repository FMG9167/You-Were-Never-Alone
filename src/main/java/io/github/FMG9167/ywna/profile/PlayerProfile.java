package io.github.FMG9167.ywna.profile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public class PlayerProfile {
    public UUID playerUUID;

    public float fearScore = 0.0f;
    public long totalObservedTicks = 0;

    public float avgMoveSpeed = 0.0f;
    public float sprintFrac = 0.0f;
    public float sneakFrac = 0.0f;
    public float cameraVariance = 0.0f;
    public int lookBehindCount = 0;

    public int sessionStartHour = 0;
    public int sessionEndHour = 0;
    public long avgSessionLengthTicks = 0;
    public long sessionStartTick = 0;
    public long totalSessions = 0;

    public BlockPos lastDeathPos = null;

    public BlockPos safeZone = null;
    public double safeZoneSumX = 0;
    public double safeZoneSumZ = 0;
    public long safeZoneSamples = 0;
    public BlockPos safeZoneDoor = null;

    public static final int LOOK_HISTORY_SIZE = 60;
    public final Deque<float[]> lookHistory = new ArrayDeque<>(LOOK_HISTORY_SIZE);

    public String[] hotbarSnapshot = new String[9];
    public int[] hotbarUsageCount = new int[9];

    public long[] lastEffectTick = new long[32];
    public int noticedEffectsMask = 0;
    public int processedNoticedMask = 0;

    public boolean doppelgangerActive = false;
    public Vec3d doppelgangerPos = null;
    public int doppelgangerSightings = 0;

    public boolean replacementComplete = false;

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putUuid("playerUUID", playerUUID);

        nbt.putFloat("fearScore", fearScore);
        nbt.putLong("totalObservedTicks", totalObservedTicks);

        nbt.putFloat("avgMoveSpeed", avgMoveSpeed);
        nbt.putFloat("sprintFrac", sprintFrac);
        nbt.putFloat("sneakFrac", sneakFrac);
        nbt.putFloat("cameraVariance", cameraVariance);
        nbt.putInt("lookBehindCount", lookBehindCount);

        nbt.putInt("sessionStartHour", sessionStartHour);
        nbt.putInt("sessionEndHour", sessionEndHour);
        nbt.putLong("avgSessionLengthTicks", avgSessionLengthTicks);
        nbt.putLong("sessionStartTick", sessionStartTick);
        nbt.putLong("totalSessions", totalSessions);

        if(lastDeathPos != null) {
            nbt.putInt("lastDeathPosX", lastDeathPos.getX());
            nbt.putInt("lastDeathPosY", lastDeathPos.getY());
            nbt.putInt("lastDeathPosZ", lastDeathPos.getZ());
        }

        if(safeZone != null) {
            nbt.putInt("safeZoneX", safeZone.getX());
            nbt.putInt("safeZoneY", safeZone.getY());
            nbt.putInt("safeZoneZ", safeZone.getZ());
        }
        nbt.putDouble("safeZoneSumX", safeZoneSumX);
        nbt.putDouble("safeZoneSumZ", safeZoneSumZ);
        nbt.putLong("safeZoneSamples", safeZoneSamples);
        if(safeZoneDoor != null) {
            nbt.putInt("safeZoneDoorX", safeZoneDoor.getX());
            nbt.putInt("safeZoneDoorY", safeZoneDoor.getY());
            nbt.putInt("safeZoneDoorZ", safeZoneDoor.getZ());
        }

        NbtList lookList = new NbtList();
        for (float[] look : lookHistory) {
            lookList.add(NbtDouble.of(look[0]));
            lookList.add(NbtDouble.of(look[1]));
        }
        nbt.put("lookHistory", lookList);

        NbtCompound hotbar = new NbtCompound();
        for (int i = 0; i < 9; i++) {
            if(hotbarSnapshot[i] != null) {
                hotbar.putString("slot" + i, hotbarSnapshot[i]);
            }
        }
        nbt.put("hotbarSnapshot", hotbar);
        nbt.putIntArray("hotbarUsageCount", hotbarUsageCount);

        nbt.putLongArray("lastEffectTick", lastEffectTick);
        nbt.putInt("noticedEffectsMask", noticedEffectsMask);
        nbt.putInt("processedNoticedMask", processedNoticedMask);

        nbt.putBoolean("doppelgangerActive", doppelgangerActive);
        if(doppelgangerPos != null) {
            nbt.putDouble("doppelgangerX", doppelgangerPos.x);
            nbt.putDouble("doppelgangerY", doppelgangerPos.y);
            nbt.putDouble("doppelgangerZ", doppelgangerPos.z);
        }
        nbt.putInt("doppelgangerSightings", doppelgangerSightings);

        nbt.putBoolean("replacementComplete", replacementComplete);

        return nbt;
    }

    public static PlayerProfile fromNbt(NbtCompound nbt) {
        PlayerProfile p = new PlayerProfile();

        p.playerUUID = nbt.getUuid("playerUUID");

        p.fearScore = nbt.getFloat("fearScore");
        p.totalObservedTicks = nbt.getLong("totalObservedTicks");

        p.avgMoveSpeed = nbt.getFloat("avgMoveSpeed");
        p.sprintFrac = nbt.getFloat("sprintFrac");
        p.sneakFrac = nbt.getFloat("sneakFrac");
        p.cameraVariance = nbt.getFloat("cameraVariance");
        p.lookBehindCount = nbt.getInt("lookBehindCount");

        p.sessionStartHour = nbt.getInt("sessionStartHour");
        p.sessionEndHour = nbt.getInt("sessionEndHour");
        p.avgSessionLengthTicks = nbt.getLong("avgSessionLengthTicks");
        p.sessionStartTick = nbt.getLong("sessionStartTick");
        p.totalSessions = nbt.getLong("totalSessions");

        if(nbt.contains("lastDeathPosX")) {
            p.lastDeathPos = new BlockPos(
                    nbt.getInt("lastDeathPosX"),
                    nbt.getInt("lastDeathPosY"),
                    nbt.getInt("lastDeathPosZ")
            );
        }

        if(nbt.contains("safeZoneX")) {
            p.safeZone = new BlockPos(
                    nbt.getInt("safeZoneX"),
                    nbt.getInt("safeZoneY"),
                    nbt.getInt("safeZoneZ")
            );
        }
        p.safeZoneSumX = nbt.getDouble("safeZoneSumX");
        p.safeZoneSumZ = nbt.getDouble("safeZoneSumZ");
        p.safeZoneSamples = nbt.getLong("safeZoneSamples");
        if(nbt.contains("safeZoneDoorX")) {
            p.safeZoneDoor = new BlockPos(
                    nbt.getInt("safeZoneDoorX"),
                    nbt.getInt("safeZoneDoorY"),
                    nbt.getInt("safeZoneDoorZ")
            );
        }

        NbtList lookList = nbt.getList("lookHistory", 6);
        p.lookHistory.clear();
        for (int i = 0; i < lookList.size() - 1; i += 2) {
            float yaw =  (float) lookList.getDouble(i);
            float pitch = (float) lookList.getDouble(i + 1);

            if(p.lookHistory.size() >= LOOK_HISTORY_SIZE) {
                p.lookHistory.pollFirst();
            }
            p.lookHistory.addLast(new float[] {yaw, pitch});
        }

        NbtCompound hotbar = nbt.getCompound("hotbarSnapshot");
        for (int i = 0; i < 9; i++) {
            String key = "slot" + i;
            p.hotbarSnapshot[i] = hotbar.contains(key) ? hotbar.getString(key) : null;
        }
        p.hotbarUsageCount = nbt.getIntArray("hotbarUsageCount");

        p.lastEffectTick = nbt.getLongArray("lastEffectTick");
        p.noticedEffectsMask = nbt.getInt("noticedEffectsMask");
        p.processedNoticedMask = nbt.getInt("processedNoticedMask");

        p.doppelgangerActive = nbt.getBoolean("doppelgangerActive");
        if(nbt.contains("doppelgangerX")) {
            p.doppelgangerPos = new Vec3d(
                    nbt.getDouble("doppelgangerX"),
                    nbt.getDouble("doppelgangerY"),
                    nbt.getDouble("doppelgangerZ")
            );
        }
        p.doppelgangerSightings = nbt.getInt("doppelgangerSightings");

        p.replacementComplete = nbt.getBoolean("replacementComplete");

        return p;
    }

    public int getPhase() {
        if(totalObservedTicks < 2400) return 0;

        if(replacementComplete) return 6;
        if(fearScore >= 0.90f) return 5;
        if(fearScore >= 0.70f) return 4;
        if(fearScore >= 0.50f) return 3;
        if(fearScore >= 0.20f) return 2;
        if(fearScore >= 0.10f) return 1;
        return 0;
    }

   public void raiseFear(float del) {
        fearScore = Math.min(1.0f,  fearScore + del);
   }

   public boolean effectOffCooldown(int effectOrdinal, long cooldownTicks, long currentTick) {
        if(effectOrdinal < 0 || effectOrdinal >= lastEffectTick.length) {return false;}

        return (currentTick - lastEffectTick[effectOrdinal]) >= cooldownTicks;
   }

   public void markEffectFired(int effectOrdinal, long currentTick) {
        if(effectOrdinal >= 0 && effectOrdinal < lastEffectTick.length) {
            lastEffectTick[effectOrdinal] = currentTick;
        }
   }

   public boolean hasNoticed(int effectOrdinal) {
        return (noticedEffectsMask & (1 << effectOrdinal)) != 0;
   }

   public void markNoticed(int effectOrdinal) {
        noticedEffectsMask |= (1 << effectOrdinal);
   }
}
