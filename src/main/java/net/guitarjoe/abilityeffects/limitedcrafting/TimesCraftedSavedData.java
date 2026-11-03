package net.guitarjoe.abilityeffects.limitedcrafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

public class TimesCraftedSavedData extends SavedData {

    public static final Codec<TimesCraftedSavedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("map").forGetter(TimesCraftedSavedData::getMap)
            ).apply(instance, TimesCraftedSavedData::new)
    );

    public static final SavedDataType<TimesCraftedSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "times_crafted"),
            TimesCraftedSavedData::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    public final HashMap<String, Integer> map = new HashMap<>();

    public TimesCraftedSavedData() {}

    private TimesCraftedSavedData(Map<String, Integer> map) {
        this.map.putAll(map);
    }

    public Map<String, Integer> getMap() {
        return this.map;
    }

    public static TimesCraftedSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}