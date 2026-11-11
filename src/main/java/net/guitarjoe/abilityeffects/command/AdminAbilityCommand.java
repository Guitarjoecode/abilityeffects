package net.guitarjoe.abilityeffects.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Set;

public class AdminAbilityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                               CommandBuildContext context, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("adminability").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("see")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(AdminAbilityCommand::AbilitySee)))

                .then(Commands.literal("craftreset")
                        .executes(AdminAbilityCommand::CraftReset))

                .then(Commands.literal("remove")
                        .then(Commands.argument("remove_player", EntityArgument.player())
                                .then(Commands.argument("ability", AbilityArgument.ability())
                                        .then(Commands.argument("activate", BoolArgumentType.bool())
                                                .executes(AdminAbilityCommand::RemoveAbility)))))

        );
    }

    private static int AbilitySee(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(commandSourceStackCommandContext,"player");
        Set<Ability> abilities = target.getAttachedOrCreate(AttachmentTypes.ACTIVE_ABILITIES);

        for (Ability ability : abilities) {
            player.sendSystemMessage(Component.literal(ability.getName()));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int RemoveAbility(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(commandSourceStackCommandContext,"remove_player");
        Ability ability = AbilityArgument.getAbility(commandSourceStackCommandContext, "ability");
        boolean activate = BoolArgumentType.getBool(commandSourceStackCommandContext, "activate");
        try {
            AbilityHandler.toggleAbility(ability, target, activate);
            player.sendSystemMessage(Component.literal("Toggled ability"));
        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("Something went wrong"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int CraftReset(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        TimesCraftedSavedData savedData = TimesCraftedSavedData.get(commandSourceStackCommandContext.getSource().getLevel());

        savedData.map.clear();
        savedData.setDirty();

        return Command.SINGLE_SUCCESS;
    }
}
