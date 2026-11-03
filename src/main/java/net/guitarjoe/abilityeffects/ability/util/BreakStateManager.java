package net.guitarjoe.abilityeffects.ability.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BreakStateManager {
    private static final Map<UUID, Integer> activeMiningPlayers = new HashMap<>();

    public static void activateForPlayer(UUID playerUuid) {
        activeMiningPlayers.put(playerUuid, 200);
    }

    public static void tickPlayerInstantBreak(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (!activeMiningPlayers.containsKey(uuid)) {
            return;
        }

        int remaining = activeMiningPlayers.get(uuid);

        if (remaining > 1) {
            activeMiningPlayers.put(uuid, remaining - 1);
        } else {
            activeMiningPlayers.remove(uuid);
        }
    }

    public static boolean isInstantBreakActive(UUID uuid) {
        return activeMiningPlayers.getOrDefault(uuid, 0) > 0;
    }

    public static boolean canInstantBreak(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getDestroySpeed(level, pos) < 0) return false;
        Block block = state.getBlock();
        return block != Blocks.OBSIDIAN && block != Blocks.CRYING_OBSIDIAN;
    }
}