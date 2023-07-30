package net.arkadiyhimself.qualityoflife.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class QoLconfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> EXP_FROM_COMPOSTER;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CAN_COBBLE_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SNEAK_TO_COBBLE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SNEAK_TO_USE_AXE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BONEMEAL_MORE;
    public static final ForgeConfigSpec.ConfigValue<Double> ANVIL_BREAK_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EASY_BOOK_COMBINING;


    static {
        BUILDER.push("Configs for Quality of Life");
        EXP_FROM_COMPOSTER = BUILDER.comment("The amount of experience you get from composter when it gives bone meal. Setting it to 0 disables the feature. Default value is 10")
                .defineInRange("Experience from composter:",10,0,256);
        CAN_COBBLE_BLOCKS = BUILDER.comment("If this variable is true, the player can «cobble» some blocks by right-clicking the with a pickaxe: it turns stone into cobblestone, stone bricks into cracked stone bricks, etc. Default value is true")
                        .define("Can cobble blocks:",true);
        SNEAK_TO_COBBLE = BUILDER.comment("If true, the player has to hold shift to «cobble» stone blocks")
                        .define("Sneak to cobble:",true);
        SNEAK_TO_USE_AXE = BUILDER.comment("If true, the player has to hold shift to strip logs, scrape off wax and oxidation when using axe")
                        .define("Sneak to use axe:",true);
        BONEMEAL_MORE = BUILDER.comment("If true, bone meal works on cacti, nether wart, sugar coats, etc.")
                        .define("Bone meal more:", true);
        ANVIL_BREAK_CHANCE = BUILDER.comment("The chance of anvil taking damage after being used. In vanilla minecraft default value is 0.12 (12%)")
                        .defineInRange("Anvil break chance:",0.12,0,1);
        EASY_BOOK_COMBINING = BUILDER.comment("If true, combining enchanted books for higher levels of enchantment in anvil will cost less levels")
                        .define("Easy book combining", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
