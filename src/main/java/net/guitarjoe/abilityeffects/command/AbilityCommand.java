package net.guitarjoe.abilityeffects.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.guitarjoe.abilityeffects.packets.packet.OpenCraftableScreenPayloadS2C;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class AbilityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext context, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("ability")
                .then(Commands.literal("craftable").executes(AbilityCommand::OpenCraftableMenu))
                .then(Commands.literal("help").executes(AbilityCommand::Help))
                .then(Commands.literal("drop")
                        .then(Commands.argument("ability", AbilityArgument.ability())
                                .executes(ctx -> DropAbility(ctx, AbilityArgument.getAbility(ctx, "ability")))
                        )
                )
        );
    }

    private static int OpenCraftableMenu(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();
        ServerLevel level = player.level();

        TimesCraftedSavedData savedData = TimesCraftedSavedData.get(level);

        ServerPlayNetworking.send(
                player,
                new OpenCraftableScreenPayloadS2C(savedData.map)
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int DropAbility(CommandContext<CommandSourceStack> commandSourceStackCommandContext, Ability ability) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();

        if (!AbilityHandler.hasability(ability, player)) {
            player.sendSystemMessage(Component.translatable("ability.abilityeffects.dont_have_ability"));
            return 0;
        }

        AbilityHandler.toggleAbility(ability, player, false);

        Item item = AbilityHandler.fromAbility(ability);

        ItemStack abilityItem = new ItemStack(item);

        boolean itemAdded = player.getInventory().add(abilityItem);

        if (!itemAdded) {
            ItemEntity itemEntity = player.drop(abilityItem, false);
            if (itemEntity != null) {
                itemEntity.setDefaultPickUpDelay();
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int Help(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();
        ServerLevel level = player.level();
        TimesCraftedSavedData savedData = TimesCraftedSavedData.get(level);

        // --- 1. HEADER & INTRODUCTION ---
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.header"));
        player.sendSystemMessage(Component.literal("§7====================================================="));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.intro"));
        player.sendSystemMessage(Component.literal(""));

        // --- 2. CONTROLS & MECHANICS ---
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.section.mechanics"));
        player.sendSystemMessage(Component.translatable(
                "command.abilityeffects.help.keybind1",
                Component.translatable("key.abilityeffects.special_ability_key_1")
        ));

        player.sendSystemMessage(Component.translatable(
                "command.abilityeffects.help.keybind2",
                Component.translatable("key.abilityeffects.special_ability_key_2")
        ));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.pvp_drop"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.void_save"));
        player.sendSystemMessage(Component.literal(""));

        // --- 3. COMMANDS ---
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.section.commands"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.cmd_craftable"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.cmd_drop"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.cmd_help"));
        player.sendSystemMessage(Component.literal(""));

        // --- 4. DETAILED ABILITIES & WORLD LIMITS ---
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.section.abilities"));

        for (Ability ability : Ability.values()) {
            String itemKey = "abilityeffects:" + ability.getName() + "_item";
            int currentlyCrafted = savedData.map.getOrDefault(itemKey, 0);

            int maxLimit = 0;
            for (Map.Entry<Item, Integer> entry : AbilityEffects.maxTimesCrafted.entrySet()) {
                if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(entry.getKey()).toString().equals(itemKey)) {
                    maxLimit = entry.getValue();
                    break;
                }
            }

            int cooldownSeconds = ability.getCooldown() / 20;
            String nameKey = ability.getName().replace("_ability", "").toUpperCase();

            player.sendSystemMessage(Component.translatable(
                    "command.abilityeffects.help.ability.meta",
                    nameKey, cooldownSeconds, currentlyCrafted, maxLimit
            ));
            player.sendSystemMessage(Component.translatable("command.abilityeffects.help.ability." + ability.getName() + ".desc"));
        }

        // --- 5. RECIPES RULES & DISCORD ---
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.section.info"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.recipes"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.discord"));
        player.sendSystemMessage(Component.translatable("command.abilityeffects.help.rules"));

        player.sendSystemMessage(Component.literal(""));

        player.sendSystemMessage(Component.literal("§7====================================================="));

        return Command.SINGLE_SUCCESS;
    }
}
