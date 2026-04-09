package com.y271727uy.ranch_festival;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder SEASON_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> SEASON_ACCESS_REQUIRED_SEASON;
    public static final ForgeConfigSpec.ConfigValue<String> SEASON_ACCESS_REQUIRED_SUBSEASON;
    public static final ForgeConfigSpec.IntValue SEASON_ACCESS_REQUIRED_DAY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_THE_DIMENSION_SEASON_LOCK;

    static {
        SEASON_BUILDER.comment("Season-based dimension access settings").push("season_access");

        ENABLE_THE_DIMENSION_SEASON_LOCK = SEASON_BUILDER
                .comment("Enable season-based restriction for accessing the 'test' dimension")
                .define("enableTheDimensionSeasonLock", true);

        SEASON_ACCESS_REQUIRED_SEASON = SEASON_BUILDER
                .comment("Required season to access the 'test' dimension (SPRING, SUMMER, AUTUMN, WINTER)")
                .define("requiredSeason", "SPRING");

        SEASON_ACCESS_REQUIRED_SUBSEASON = SEASON_BUILDER
                .comment("Required sub-season to access the 'test' dimension (EARLY_SPRING, MID_SPRING, LATE_SPRING, etc.)")
                .define("requiredSubSeason", "EARLY_SPRING");

        SEASON_ACCESS_REQUIRED_DAY = SEASON_BUILDER
                .comment("Required day of the season to access the 'test' dimension (1-24)")
                .defineInRange("requiredDayOfSeason", 2, 1, 24);

        SEASON_BUILDER.pop();
    }

    public static final ForgeConfigSpec SEASON_SPEC = SEASON_BUILDER.build();

    public static boolean enableTheDimensionSeasonLock;
    public static String requiredSeason;
    public static String requiredSubSeason;
    public static int requiredDayOfSeason;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        refreshConfig();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent event) {
        refreshConfig();
    }

    public static void refreshConfig() {
        enableTheDimensionSeasonLock = ENABLE_THE_DIMENSION_SEASON_LOCK.get();
        
        requiredSeason = SEASON_ACCESS_REQUIRED_SEASON.get();
        requiredSubSeason = SEASON_ACCESS_REQUIRED_SUBSEASON.get();
        requiredDayOfSeason = SEASON_ACCESS_REQUIRED_DAY.get();
    }
}