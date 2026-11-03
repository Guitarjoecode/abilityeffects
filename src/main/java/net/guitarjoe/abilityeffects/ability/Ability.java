package net.guitarjoe.abilityeffects.ability;

public enum Ability {
    SPEED_ABILITY("speed_ability", 1800),
    FIRE_RESISTANCE_ABILITY("fire_resistance_ability", 6000),
    HEALTH_BOOST_ABILITY("health_boost_ability", 6000),
    FEATHER_FALLING_ABILITY("feather_falling_ability", 2400),
    STRENGTH_ABILITY("strength_ability", 4800),
    MINING_ABILITY("mining_ability", 8400),
    WARDEN_ABILITY("warden_ability", 4800),
    WITHER_ABILITY("wither_ability", 3600),
    PULL_ABILITY("pull_ability", 6000),
    TRACKER_ABILITY("tracker_ability", 36000),
    ENDER_DRAGON_ABILITY("ender_dragon_ability", 12000);

    private final String name;
    private final int cooldown;

    Ability(final String name, final int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public String getName() {
        return this.name;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
