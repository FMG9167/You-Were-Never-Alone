package io.github.FMG9167.ywna.profile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class ProfileManager {
    private static final ProfileManager INSTANCE = new ProfileManager();
    public static ProfileManager getInstance() {return INSTANCE;}

    public static final String NBT_KEY = "ywna_key";

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();
    public final Set<UUID> disconnecting = new HashSet<>();

    private ProfileManager() {}

    public static PlayerProfile get(ServerPlayerEntity player) {
        ProfileManager mgr = INSTANCE;
        UUID uuid = player.getUuid();
        if (!mgr.profiles.containsKey(uuid)) {
            PlayerProfile fresh = new PlayerProfile();
            fresh.playerUUID = uuid;
            mgr.profiles.put(uuid, fresh);
        }
        return mgr.profiles.get(uuid);
    }

    public void loadFromNbt(ServerPlayerEntity player, NbtCompound nbt) {
        UUID uuid = player.getUuid();
        if(nbt.contains(NBT_KEY)) {
            PlayerProfile p = PlayerProfile.fromNbt(nbt.getCompound(NBT_KEY));
            p.playerUUID = uuid;
            profiles.put(uuid, p);
        } else {
            PlayerProfile fresh = new PlayerProfile();
            fresh.playerUUID = uuid;
            profiles.put(uuid, fresh);
        }
    }

    public void saveToNbt(ServerPlayerEntity player, NbtCompound nbt) {
        UUID uuid = player.getUuid();
        PlayerProfile p = profiles.get(uuid);
        if(p != null) {
            nbt.put(NBT_KEY, p.toNbt());
        }
    }

    public void unload(ServerPlayerEntity player) {
        profiles.remove(player.getUuid());
    }

    public boolean hasProfile(ServerPlayerEntity player) {
        return profiles.containsKey(player.getUuid());
    }

    public void markDisconnecting(ServerPlayerEntity player) {
        disconnecting.add(player.getUuid());
    }

    public boolean isDisconnecting(ServerPlayerEntity player) {
        return disconnecting.contains(player.getUuid());
    }

}
