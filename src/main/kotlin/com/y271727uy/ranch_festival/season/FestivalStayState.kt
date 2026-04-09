package com.y271727uy.ranch_festival.season

import com.y271727uy.ranch_festival.RanchFestivalMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer

object FestivalStayState {
    private const val ROOT_KEY = "${RanchFestivalMod.MODID}_festival_stay"
    private const val ENTRY_DAY_KEY = "entryDay"

    @JvmStatic
    fun getEntryDay(player: ServerPlayer): Int? {
        val root = player.persistentData
        if (!root.contains(ROOT_KEY, Tag.TAG_COMPOUND.toInt())) {
            return null
        }

        val data = root.getCompound(ROOT_KEY)
        return if (data.contains(ENTRY_DAY_KEY, Tag.TAG_INT.toInt())) data.getInt(ENTRY_DAY_KEY) else null
    }

    @JvmStatic
    @Suppress("unused")
    fun isTracked(player: ServerPlayer): Boolean = getEntryDay(player) != null

    @JvmStatic
    @Suppress("unused")
    fun isExpired(player: ServerPlayer, currentDay: Int): Boolean {
        val entryDay = getEntryDay(player) ?: return false
        return currentDay > 0 && currentDay != entryDay
    }

    @JvmStatic
    fun recordEntryDay(player: ServerPlayer, dayOfSeason: Int) {
        if (dayOfSeason <= 0) {
            clear(player)
            return
        }

        val data = CompoundTag().apply {
            putInt(ENTRY_DAY_KEY, dayOfSeason)
        }
        player.persistentData.put(ROOT_KEY, data)
    }

    @JvmStatic
    fun clear(player: ServerPlayer) {
        player.persistentData.remove(ROOT_KEY)
    }
}


