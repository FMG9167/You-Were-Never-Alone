package io.github.FMG9167.ywna.effects;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MissingItemEffect {
    private static final long NORMAL_RESTORE_TICKS = 600L;
    private static final long WIPE_RESTORE_TICKS = 100L;
    private static final float WIPE_CHANCE = 0.15f;
    private static final long WIPE_COOLDOWN_TICKS = 72000L;

    private static final Random RANDOM = new Random();


    public static void fire(ServerPlayerEntity player, PlayerProfile profile, long currentTick) {
        if(profile.wipedInventory != null || !profile.missingItem.isEmpty()) return;

        boolean canWipe = profile.getPhase() >= 4 &&
                profile.fearScore >= 0.70f &&
                RANDOM.nextFloat() < WIPE_CHANCE &&
                (currentTick - profile.lastWipeTick) >= WIPE_COOLDOWN_TICKS;

        if(canWipe) {
            fireWipe(player, profile, currentTick);
        } else {
            fireNormal(player, profile, currentTick);
        }
    }


    private static void fireNormal(ServerPlayerEntity player, PlayerProfile profile, long currentTick) {
        int heldSlot = player.getInventory().selectedSlot;

        List<Integer> candidates = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            if(i == heldSlot) continue;
            if(!player.getInventory().getStack(i).isEmpty()) {
                candidates.add(i);
            }
        }

        if(candidates.isEmpty()) return;

        int slot = candidates.get(RANDOM.nextInt(candidates.size()));
        ItemStack item =  player.getInventory().getStack(slot).copy();

        player.getInventory().setStack(slot, ItemStack.EMPTY);
        player.playerScreenHandler.syncState();

        profile.missingItem = item;
        profile.missingItemSlot = slot;
        profile.missingItemRestoreTick = currentTick +  NORMAL_RESTORE_TICKS;

        YWNAMod.LOGGER.info("[YWNA] MissingItem: Removed {} from slot {}", item.getItem(), slot);
    }

    private static void fireWipe(ServerPlayerEntity player, PlayerProfile profile, long currentTick) {
        DefaultedList<ItemStack> saved = DefaultedList.ofSize(player.getInventory().size(), ItemStack.EMPTY);

        for (int i = 0; i < player.getInventory().size(); i++) {
            saved.set(i, player.getInventory().getStack(i));
            player.getInventory().setStack(i, ItemStack.EMPTY);
        }

        player.playerScreenHandler.syncState();

        profile.wipedInventory = saved;
        profile.wipeRestoreTick = currentTick +  WIPE_RESTORE_TICKS;
        profile.lastWipeTick = currentTick;

        YWNAMod.LOGGER.info("[YWNA] MissingItem: Full inventory wiped");
    }

    public static void checkRestore(ServerPlayerEntity player, PlayerProfile profile, long currentTick) {
        if(profile.missingItem.isEmpty() && currentTick >= profile.missingItemRestoreTick) {
            restoreNormal(player, profile);
        }

        if(profile.wipedInventory != null && currentTick >= profile.wipeRestoreTick) {
            restoreWipe(player, profile);
        }
    }

    public static void onInventoryOpen(ServerPlayerEntity player, PlayerProfile profile) {
        if(profile.missingItem.isEmpty()) return;

        if(!profile.missingItemSeen) {
            profile.missingItemSeen = true;
            profile.markNoticed(EffectType.MISSING_ITEM.ordinalIndex);
            return;
        }

        restoreNormal(player, profile);
    }


    private static void restoreNormal(ServerPlayerEntity player, PlayerProfile profile) {
        if(profile.missingItemSlot >= 0 && profile.missingItemSlot < 9) {
            ItemStack currentInSlot = player.getInventory().getStack(profile.missingItemSlot);

            if(!currentInSlot.isEmpty()) {
                int emptySlot = player.getInventory().getEmptySlot();
                if(emptySlot == -1) {
                    player.dropItem(currentInSlot, false);
                } else {
                    player.getInventory().setStack(emptySlot, currentInSlot.copy());
                }
            }

            player.getInventory().setStack(profile.missingItemSlot, profile.missingItem);
            player.playerScreenHandler.syncState();
        }

        profile.missingItem = ItemStack.EMPTY;
        profile.missingItemSlot = -1;
        profile.missingItemRestoreTick = -1L;
        profile.missingItemSeen = false;

        YWNAMod.LOGGER.info("[YWNA] MissingItem: Item restored");
    }

    private static void restoreWipe(ServerPlayerEntity player, PlayerProfile profile) {
        for(int i = 0; i < profile.wipedInventory.size() && i < player.getInventory().size(); i++) {
            if(!player.getInventory().getStack(i).isEmpty()) {
                player.dropItem(player.getInventory().getStack(i), false);
            }
            player.getInventory().setStack(i, profile.wipedInventory.get(i));
        }
        player.playerScreenHandler.syncState();
        profile.wipedInventory = null;
        profile.wipeRestoreTick = -1L;

        YWNAMod.LOGGER.info("[YWNA] MissingItem: Wipe restored");
    }
}
