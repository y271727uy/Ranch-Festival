package com.y271727uy.ranch_festival.season;

import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.Config;
import com.y271727uy.ranch_festival.dimension.DimensionAccessController;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import com.y271727uy.ranch_festival.dimension.DimensionRegistry;

/**
 * 季节访问检查器，用于控制玩家在特定季节和日期进入维度
 */
@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeasonAccessChecker {

    /**
     * 检查当前季节是否允许进入 test 维度
     * 条件：根据配置确定
     * @return true如果允许进入，false否则
     */
    @SuppressWarnings("unused")
    public static boolean isTestDimensionAccessAllowed(Level level) {
        return isDimensionAccessAllowed(level, DimensionRegistry.findByDimension(level.dimension()));
    }

    public static boolean isDimensionAccessAllowed(Level level, DimensionDefinition definition) {
        if (!Config.enableTheDimensionSeasonLock) {
            return true;
        }

        if (definition == null) {
            return false;
        }

        ISeasonState seasonInfo = getSeasonState(level);
        Season season = seasonInfo.getSeason();
        Season.SubSeason currentSubSeason = seasonInfo.getSubSeason();
        int currentDayOfSeason = getDayOfSeason(seasonInfo);

        boolean isCorrectSeason = definition.matchesSeason(season);
        boolean isCorrectSubSeason = definition.matchesSubSeason(currentSubSeason);
        boolean isCorrectDay = definition.matchesDay(currentDayOfSeason);

        RanchFestivalMod.LOGGER.debug("Season check - Current: {}, Match: {}", season, isCorrectSeason);
        RanchFestivalMod.LOGGER.debug("Sub-season check - Current: {}, Match: {}", currentSubSeason, isCorrectSubSeason);
        RanchFestivalMod.LOGGER.debug("Day check - Current: {}, Required: {}, Match: {}", currentDayOfSeason, definition.getDay(), isCorrectDay);

        return isCorrectSeason && isCorrectSubSeason && isCorrectDay;
    }

    public static ISeasonState getSeasonState(Level level) {
        return SeasonHelper.getSeasonState(resolveSeasonLevel(level));
    }

    @SuppressWarnings("unused")
    public static int getTicksUntilDayEnds(Level level) {
        boolean sereneSeasonsLoaded = SeasonApiHelper.hasSereneSeasons();
        if (!sereneSeasonsLoaded) {
            return -1;
        }

        ISeasonState seasonInfo = getSeasonState(level);
        int dayDuration = seasonInfo.getDayDuration();
        int seasonCycleTicks = seasonInfo.getSeasonCycleTicks();

        if (dayDuration <= 0 || seasonCycleTicks < 0) {
            return -1;
        }

        int ticksIntoCurrentDay = seasonCycleTicks % dayDuration;
        int ticksRemaining = dayDuration - ticksIntoCurrentDay;
        return ticksRemaining > 0 ? ticksRemaining : dayDuration;
    }

    /**
     * 检查玩家是否可以进入特定维度
     * @param event 维度传送事件
     */
    @SubscribeEvent
    public static void onPlayerTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 检查是否试图进入受季节控制的节日维度
        ResourceKey<Level> targetDimension = event.getDimension();
        DimensionDefinition definition = DimensionRegistry.findByDimension(targetDimension);
        if (definition != null) {
            if (!DimensionAccessController.canEnterDimension(player, targetDimension, definition)) {
                player.displayClientMessage(
                    Component.translatable("ranch_festival.message.dimension_season_locked", describeDimension(definition)),
                    true
                );
                event.setCanceled(true); // 阻止传送
            }
        }
    }

    /**
     * 获取当前季节信息的字符串描述
     * @return 季节信息描述
     */
    @SuppressWarnings("unused")
    public static String getCurrentSeasonInfo() {
        boolean sereneSeasonsLoaded = SeasonApiHelper.hasSereneSeasons();
        if (!sereneSeasonsLoaded) {
            return Component.translatable("ranch_festival.message.serene_seasons_unavailable").getString();
        }

        String currentSeason = SeasonApiHelper.getCurrentSeasonString();
        String currentSubSeason = SeasonApiHelper.getCurrentSubSeasonString();
        int currentDayOfSeason = SeasonApiHelper.getDayOfSeason();

        return Component.translatable(
                "ranch_festival.message.current_season_info",
                Component.translatable(seasonKey(currentSeason)),
                Component.translatable(subSeasonKey(currentSubSeason)),
                currentDayOfSeason
        ).getString();
    }
    

    public static int getDayOfSeason(Level level) {
        return getDayOfSeason(getSeasonState(level));
    }

    private static int getDayOfSeason(ISeasonState seasonInfo) {
        int dayInSubSeason = getDayOfCurrentSubSeason(seasonInfo);
        int daysPerSubSeason = getDaysPerSubSeason(seasonInfo);
        int subSeasonIndex = switch (seasonInfo.getSubSeason()) {
            case EARLY_SPRING, EARLY_SUMMER, EARLY_AUTUMN, EARLY_WINTER -> 0;
            case MID_SPRING, MID_SUMMER, MID_AUTUMN, MID_WINTER -> 1;
            case LATE_SPRING, LATE_SUMMER, LATE_AUTUMN, LATE_WINTER -> 2;
        };

        if (dayInSubSeason <= 0 || daysPerSubSeason <= 0) {
            return -1;
        }

        return subSeasonIndex * daysPerSubSeason + dayInSubSeason;
    }

    private static int getDayOfCurrentSubSeason(ISeasonState seasonInfo) {
        int daysPerSubSeason = getDaysPerSubSeason(seasonInfo);
        if (daysPerSubSeason <= 0) {
            return -1;
        }

        return (seasonInfo.getSeasonCycleTicks() / seasonInfo.getDayDuration()) % daysPerSubSeason + 1;
    }

    private static int getDaysPerSubSeason(ISeasonState seasonInfo) {
        int dayDuration = seasonInfo.getDayDuration();
        int subSeasonDuration = seasonInfo.getSubSeasonDuration();

        if (dayDuration <= 0 || subSeasonDuration <= 0) {
            return -1;
        }

        return subSeasonDuration / dayDuration;
    }

    private static Level resolveSeasonLevel(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) {
                return overworld;
            }
        }

        return level;
    }

    private static String describeDimension(DimensionDefinition definition) {
        return definition.getStructureDimension() == null
                ? Component.translatable("ranch_festival.message.unknown_dimension").getString()
                : definition.getStructureDimension().location().toString();
    }

    private static String seasonKey(String seasonName) {
        return switch (seasonName) {
            case "SPRING" -> "ranch_festival.season.spring";
            case "SUMMER" -> "ranch_festival.season.summer";
            case "AUTUMN" -> "ranch_festival.season.autumn";
            case "WINTER" -> "ranch_festival.season.winter";
            default -> "ranch_festival.season.unknown";
        };
    }

    private static String subSeasonKey(String subSeasonName) {
        return switch (subSeasonName) {
            case "EARLY_SPRING" -> "ranch_festival.subseason.early_spring";
            case "MID_SPRING" -> "ranch_festival.subseason.mid_spring";
            case "LATE_SPRING" -> "ranch_festival.subseason.late_spring";
            case "EARLY_SUMMER" -> "ranch_festival.subseason.early_summer";
            case "MID_SUMMER" -> "ranch_festival.subseason.mid_summer";
            case "LATE_SUMMER" -> "ranch_festival.subseason.late_summer";
            case "EARLY_AUTUMN" -> "ranch_festival.subseason.early_autumn";
            case "MID_AUTUMN" -> "ranch_festival.subseason.mid_autumn";
            case "LATE_AUTUMN" -> "ranch_festival.subseason.late_autumn";
            case "EARLY_WINTER" -> "ranch_festival.subseason.early_winter";
            case "MID_WINTER" -> "ranch_festival.subseason.mid_winter";
            case "LATE_WINTER" -> "ranch_festival.subseason.late_winter";
            default -> "ranch_festival.subseason.unknown";
        };
    }
}