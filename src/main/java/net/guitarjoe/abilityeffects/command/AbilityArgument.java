package net.guitarjoe.abilityeffects.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AbilityArgument implements ArgumentType<Ability> {

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ABILITY = new DynamicCommandExceptionType(
            name -> Component.translatable("ability.abilityeffects.not_found", name)
    );

    public static AbilityArgument ability() {
        return new AbilityArgument();
    }

    public static Ability getAbility(CommandContext<?> context, String name) {
        return context.getArgument(name, Ability.class);
    }

    @Override
    public Ability parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String name = reader.readUnquotedString();

        for (Ability ability : Ability.values()) {
            if (ability.getName().equals(name)) {
                return ability;
            }
        }

        reader.setCursor(start);
        throw ERROR_UNKNOWN_ABILITY.createWithContext(reader, name);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSourceStack sourceStack) {
            ServerPlayer serverPlayer = sourceStack.getPlayer();

            if (serverPlayer != null) {
                Set<Ability> activeAbilities = serverPlayer.getAttachedOrCreate(
                        AttachmentTypes.ACTIVE_ABILITIES,
                        HashSet::new
                );

                List<String> names = activeAbilities.stream().map(Ability::getName).toList();
                return SharedSuggestionProvider.suggest(names, builder);
            }
        }

        return builder.buildFuture();
    }
}