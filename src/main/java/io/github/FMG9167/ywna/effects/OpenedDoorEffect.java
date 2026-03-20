package io.github.FMG9167.ywna.effects;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OpenedDoorEffect {
    public static final int SEARCH_RADIUS = 32;

    public static void fire(ServerPlayerEntity player, PlayerProfile profile) {
        ServerWorld world = player.getServerWorld();
        BlockPos playerPos = player.getBlockPos();

        List<BlockPos> doors = findClosedDoors(world, playerPos);
        if (doors.isEmpty()) {
            YWNAMod.LOGGER.info("[YWNA] OpenedDoor: No doors found.");
            return;
        }

        doors.sort( Comparator.comparingDouble(pos -> pos.getSquaredDistance(playerPos)) );
        BlockPos doorPos = doors.get(0);

        openDoor(world, doorPos);
        profile.markNoticed(EffectType.OPENED_DOOR.ordinalIndex);
        YWNAMod.LOGGER.info("[YWNA] OpenedDoor: Opened door at: {}", doorPos);
    }

    private static List<BlockPos> findClosedDoors(ServerWorld world, BlockPos center) {
        List<BlockPos> found = new ArrayList<>();

        BlockPos.iterate(
                center.add(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                center.add(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS)
        ).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof DoorBlock && state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER && ( !(state.get(DoorBlock.OPEN)) )  ) {
                found.add(pos.toImmutable());
            }
        });

        return found;
    }

    private static void openDoor(ServerWorld world, BlockPos pos) {
        BlockState lower = world.getBlockState(pos);
        BlockPos upperPos = pos.up();
        BlockState upper = world.getBlockState(upperPos);

        if( !(lower.getBlock() instanceof DoorBlock) ) return;

        world.setBlockState(pos, lower.with(DoorBlock.OPEN, true), Block.NOTIFY_ALL);
        world.setBlockState(upperPos, upper.with(DoorBlock.OPEN, true), Block.NOTIFY_ALL);

        world.syncWorldEvent(null, 1006, pos, 0);
    }
}
