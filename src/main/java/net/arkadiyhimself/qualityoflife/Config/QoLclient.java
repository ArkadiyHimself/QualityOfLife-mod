package net.arkadiyhimself.qualityoflife.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class QoLclient {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOTBAR_DURABILITY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INVENTORY_DURABILITY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OFF_HAND_DURABILITY;
    static {
        BUILDER.push("Config for Quality of Life renders");

        HOTBAR_DURABILITY = BUILDER.comment("If true, the durability of item held in main hand is shown in hotbar")
                        .define("Hotbar durability:",true);
        INVENTORY_DURABILITY = BUILDER.comment("If true, item's description will show its durability")
                        .define("Inventory durability:", true);
        OFF_HAND_DURABILITY = BUILDER.comment("If true, the durability of item in offhand will be shown above it")
                        .define("Offhand durability:", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
