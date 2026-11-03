package net.guitarjoe.abilityeffects.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.ability.util.AttributeHandler;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonEntity;
import net.guitarjoe.abilityeffects.packets.packet.SyncAbilitiesPayloadS2C;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.net.URI;
import java.util.*;

public class ModPlayerEvents {
    public static void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayer player = serverGamePacketListener.getPlayer();

        Map<Ability, Integer> cooldowns = player.getAttached(AttachmentTypes.COOLDOWNS);
        if (cooldowns != null && !cooldowns.isEmpty()) {
            player.setAttached(AttachmentTypes.COOLDOWNS, new HashMap<>(cooldowns));
        } else {
            player.setAttached(AttachmentTypes.COOLDOWNS, new HashMap<>());
        }

        Set<Ability> active = player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
        if (active != null && !active.isEmpty()) {
            player.setAttached(AttachmentTypes.ACTIVE_ABILITIES, new HashSet<>(active));
        } else {
            player.setAttached(AttachmentTypes.ACTIVE_ABILITIES, new HashSet<>());
        }

        Collection<RecipeHolder<?>> allRecipes = minecraftServer.getRecipeManager().getRecipes();
        List<RecipeHolder<?>> modRecipes = new ArrayList<>();
        for (RecipeHolder<?> recipe : allRecipes) {
            if (recipe.id().registry().getNamespace().equals(AbilityEffects.MOD_ID)) {
                modRecipes.add(recipe);
            }
        }
        if (!modRecipes.isEmpty()) {
            player.awardRecipes(modRecipes);
        }

        minecraftServer.execute(() -> {
            boolean hasClientMod = ServerPlayNetworking.canSend(player, SyncAbilitiesPayloadS2C.TYPE);

            if (!hasClientMod) {
                player.sendSystemMessage(Component.literal("§7-------------------------------------------------"));
                player.sendSystemMessage(Component.translatable("warning.abilityeffects.no_client_mod_1"));
                player.sendSystemMessage(Component.translatable("warning.abilityeffects.no_client_mod_2"));

                String url = "https://github.com/Guitarjoecode";
                Style linkStyle = Style.EMPTY
                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(url)))
                        .withColor(ChatFormatting.GREEN);

                player.sendSystemMessage(Component.translatable("warning.abilityeffects.github_link").withStyle(linkStyle));
                player.sendSystemMessage(Component.literal("§7-------------------------------------------------"));
            }
        });
    }

    public static void onPlayerDisconnect(ServerGamePacketListenerImpl serverGamePacketListener, MinecraftServer minecraftServer) {
        ServerPlayer player = serverGamePacketListener.getPlayer();

        if (player.getVehicle() instanceof RidableEnderDragonEntity dragon) {
            dragon.discard();
        }
    }

    public static void onPlayerCopyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (!alive) {

            Set<Ability> remainingAbilities = oldPlayer.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
            if (remainingAbilities != null && !remainingAbilities.isEmpty()) {
                newPlayer.setAttached(AttachmentTypes.ACTIVE_ABILITIES, remainingAbilities);

                for (Ability ability : remainingAbilities) {
                    AttributeHandler.toggleAbilityAttribute(ability, newPlayer, true);
                }
            }

            Map<Ability, Integer> oldCooldowns = oldPlayer.getAttached(AttachmentTypes.COOLDOWNS);
            if (oldCooldowns != null && !oldCooldowns.isEmpty()) {
                newPlayer.setAttached(AttachmentTypes.COOLDOWNS, oldCooldowns);
            }

            MinecraftServer server = newPlayer.level().getServer();

            server.execute(() -> server.execute(() -> {
                Set<Ability> currentAbilities = newPlayer.getAttached(AttachmentTypes.ACTIVE_ABILITIES);

                if (currentAbilities != null) {
                    ServerPlayNetworking.send(newPlayer, new SyncAbilitiesPayloadS2C(currentAbilities));
                }
            }));
        }
    }
}
