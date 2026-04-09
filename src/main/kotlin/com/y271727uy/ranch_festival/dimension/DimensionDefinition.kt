package com.y271727uy.ranch_festival.dimension

import com.y271727uy.ranch_festival.RanchFestivalMod
import java.util.Collections
import java.util.LinkedHashSet
import java.util.Objects
import java.util.function.Supplier
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.registries.RegistryObject
import sereneseasons.api.season.Season

class DimensionDefinition private constructor(builder: Builder) {
    val structure: ResourceLocation? = builder.structureValue
    val structureAxis: BlockPos? = builder.structureAxisValue
    val structureDimension: ResourceKey<Level>? = builder.structureDimensionValue
    val playerInputAxis: BlockPos? = builder.playerInputAxisValue
    val seasons: Set<Season>? = builder.seasonsValue
    val subSeason: Season.SubSeason? = builder.subSeasonValue
    val day: Int? = builder.dayValue
    val survivalPlayerDestroy: Boolean? = builder.survivalPlayerDestroyValue
    val autoReset: Boolean? = builder.autoResetValue
    val mobSpawn: Boolean? = builder.mobSpawnValue
    private val invitation: Supplier<out Item>? = builder.invitationValue

    fun getInvitationItem(): Item? = invitation?.get()

    fun matchesInvitation(item: Item?): Boolean {
        return invitation != null && item != null && getInvitationItem() === item
    }

    fun matchesSeason(season: Season?): Boolean {
        return seasons.isNullOrEmpty() || (season != null && seasons.contains(season))
    }

    fun matchesSubSeason(currentSubSeason: Season.SubSeason?): Boolean {
        return subSeason == null || Objects.equals(subSeason, currentSubSeason)
    }

    fun matchesDay(currentDay: Int): Boolean {
        return day == null || day == currentDay
    }

    companion object {
        @JvmStatic
        fun create(): Builder = Builder()
    }

    class Builder {
        var structureValue: ResourceLocation? = null
        var structureAxisValue: BlockPos? = null
        var structureDimensionValue: ResourceKey<Level>? = null
        var playerInputAxisValue: BlockPos? = null
        var seasonsValue: Set<Season>? = null
        var subSeasonValue: Season.SubSeason? = null
        var dayValue: Int? = null
        var survivalPlayerDestroyValue: Boolean? = null
        var autoResetValue: Boolean? = null
        var mobSpawnValue: Boolean? = null
        var invitationValue: Supplier<out Item>? = null

        fun structure(structurePath: String?): Builder = apply {
            structureValue = structurePath?.let { ResourceLocation.fromNamespaceAndPath(RanchFestivalMod.MODID, it) }
        }

        fun structure(structure: ResourceLocation?): Builder = apply {
            structureValue = structure
        }

        fun structureAxis(x: Int, y: Int, z: Int): Builder = apply {
            structureAxisValue = BlockPos(x, y, z)
        }

        fun structureAxis(structureAxis: BlockPos?): Builder = apply {
            structureAxisValue = structureAxis
        }

        fun structureDimension(structureDimension: ResourceKey<Level>?): Builder = apply {
            structureDimensionValue = structureDimension
        }

        fun playerInputAxis(x: Int, y: Int, z: Int): Builder = apply {
            playerInputAxisValue = BlockPos(x, y, z)
        }

        fun playerInputAxis(playerInputAxis: BlockPos?): Builder = apply {
            playerInputAxisValue = playerInputAxis
        }

        fun seasons(vararg seasons: Season?): Builder {
            if (seasons.isEmpty()) {
                seasonsValue = null
                return this
            }

            val configuredSeasons = LinkedHashSet<Season>()
            seasons.filterNotNull().forEach(configuredSeasons::add)
            seasonsValue = if (configuredSeasons.isEmpty()) null else Collections.unmodifiableSet(configuredSeasons)
            return this
        }

        fun subSeason(subSeason: Season.SubSeason?): Builder = apply {
            subSeasonValue = subSeason
        }

        fun day(day: Int?): Builder = apply {
            dayValue = day
        }

        fun survivalPlayerDestory(survivalPlayerDestroy: Boolean?): Builder = apply {
            survivalPlayerDestroyValue = survivalPlayerDestroy
        }

        fun survivalPlayerDestroy(survivalPlayerDestroy: Boolean?): Builder = survivalPlayerDestory(survivalPlayerDestroy)

        fun autoReset(autoReset: Boolean?): Builder = apply {
            autoResetValue = autoReset
        }

        fun mobSpawn(mobSpawn: Boolean?): Builder = apply {
            mobSpawnValue = mobSpawn
        }

        fun invitation(invitation: Item?): Builder = invitation(invitation?.let { Supplier { it } })

        fun invitation(invitation: RegistryObject<out Item>?): Builder = invitation(invitation?.let { Supplier { it.get() } })

        fun invitation(invitation: Supplier<out Item>?): Builder = apply {
            invitationValue = invitation
        }

        fun build(): DimensionDefinition = DimensionDefinition(this)

        fun register(): DimensionDefinition = DimensionRegistry.register(build())
    }
}

fun dimensionDefinition(block: DimensionDefinition.Builder.() -> Unit): DimensionDefinition.Builder {
    return DimensionDefinition.create().apply(block)
}




