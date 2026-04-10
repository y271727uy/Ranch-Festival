package com.y271727uy.ranch_festival.client

import com.y271727uy.ranch_festival.dimension.DimensionRegistry
import com.y271727uy.ranch_festival.season.SeasonAccessChecker
import com.y271727uy.ranch_festival.season.SeasonApiHelper
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level

/**
 * 已解析的“今日节日”提示。
 */
data class FestivalTodayHintState(
    val key: String,
    val message: Component
)

object FestivalTodayHint {
    private const val KEY_TODAY_IS_FESTIVAL = "ranch_festival.message.today_is_festival"

    @JvmStatic
    fun resolveTodayFestivalHint(level: Level?): FestivalTodayHintState? {
        if (level == null || !SeasonApiHelper.hasSereneSeasons()) {
            return null
        }

        val currentDay = SeasonAccessChecker.getDayOfSeason(level)
        if (currentDay <= 0) {
            return null
        }

        val seasonInfo = SeasonAccessChecker.getSeasonState(level)
        val festival = DimensionRegistry.all().firstOrNull { definition ->
            val festivalNameKey = definition.festivalNameKey
            !festivalNameKey.isNullOrBlank() &&
                definition.matchesSeason(seasonInfo.season) &&
                definition.matchesSubSeason(seasonInfo.subSeason) &&
                definition.matchesDay(currentDay)
        } ?: return null

        val festivalNameKey = festival.festivalNameKey ?: return null
        val stableKey = buildString {
            append(festivalNameKey)
            append('|')
            append(seasonInfo.season)
            append('|')
            append(seasonInfo.subSeason)
            append('|')
            append(currentDay)
        }

        return FestivalTodayHintState(
            key = stableKey,
            message = Component.translatable(KEY_TODAY_IS_FESTIVAL, Component.translatable(festivalNameKey))
        )
    }

    @JvmStatic
    fun getTodayFestivalMessage(level: Level?): Component? = resolveTodayFestivalHint(level)?.message
}
