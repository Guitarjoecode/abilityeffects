package net.guitarjoe.abilityeffects.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyMappings {
    public static final KeyMapping.Category ABILITY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "abilityeffects")
    );

    public static final KeyMapping SPECIAL_ABILITY_KEYBIND_1 = new KeyMapping(
            "key.abilityeffects.special_ability_key_1",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            ABILITY_CATEGORY
    );

    public static final KeyMapping SPECIAL_ABILITY_KEYBIND_2 = new KeyMapping(
            "key.abilityeffects.special_ability_key_2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            ABILITY_CATEGORY
    );

    public static void register() {
        KeyMappingHelper.registerKeyMapping(SPECIAL_ABILITY_KEYBIND_1);
        KeyMappingHelper.registerKeyMapping(SPECIAL_ABILITY_KEYBIND_2);
    }
}