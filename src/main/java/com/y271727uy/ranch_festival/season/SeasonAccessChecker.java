package com.y271727uy.ranch_festival.season;

import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.Config;
import com.y271727uy.ranch_festival.dimension.DimensionAccessController;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;

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
    public static boolean isTestDimensionAccessAllowed(Level level) {
        return isDimensionAccessAllowed(level, DimensionDefinition.TEST);
    }

    public static boolean isDimensionAccessAllowed(Level level, DimensionDefinition definition) {
        if (!Config.enableTheDimensionSeasonLock) {
            return true;
        }

        if (definition == null) {
            return false;
        }

        ISeasonState seasonInfo = SeasonHelper.getSeasonState(level);
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
        DimensionDefinition definition = DimensionDefinition.findByDimension(targetDimension);
        if (definition != null) {
            if (!DimensionAccessController.canEnterDimension(player, targetDimension, definition)) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "当前季节不允许进入 " + definition.getStructureDimension().location() + "！未满足当前节日配置。"), true);
                event.setCanceled(true); // 阻止传送
            }
        }
    }

    /**
     * 获取当前季节信息的字符串描述
     * @return 季节信息描述
     */
    public static String getCurrentSeasonInfo() {
        if (!SeasonApiHelper.isSereneSeasonsLoaded()) {
            return "Serene Seasons未安装或未加载";
        }

        String currentSeason = SeasonApiHelper.getCurrentSeasonString();
        String currentSubSeason = SeasonApiHelper.getCurrentSubSeasonString();
        int currentDayOfSeason = SeasonApiHelper.getDayOfSeason();

        return String.format("当前季节: %s, 子季节: %s, 季节第%d天", 
                           currentSeason, 
                           currentSubSeason, 
                           currentDayOfSeason);
    }
    

    public static int getDayOfSeason(Level level) {
        return getDayOfSeason(SeasonHelper.getSeasonState(level));
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
}