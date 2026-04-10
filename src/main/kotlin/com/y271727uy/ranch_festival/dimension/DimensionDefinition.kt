package com.y271727uy.ranch_festival.dimension

import com.y271727uy.ranch_festival.RanchFestivalMod
import java.util.Collections
import java.util.LinkedHashSet
import java.util.Objects
import java.util.function.Supplier
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
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
    val dimensionNameKey: String? = builder.dimensionNameKeyValue
    val festivalNameKey: String? = builder.festivalNameKeyValue
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

    @Suppress("unused")
    fun getDimensionDisplayName(): Component {
        val key = dimensionNameKey ?: festivalNameKey
        if (!key.isNullOrBlank()) {
            return Component.translatable(key)
        }

        val dimensionId = structureDimension?.location()?.toString()
        return if (!dimensionId.isNullOrBlank()) {
            Component.literal(dimensionId)
        } else {
            Component.translatable("ranch_festival.message.unknown_dimension")
        }
    }

    fun resolveStructureClearBounds(level: ServerLevel, padding: Int = 3): StructureClearBounds? {
        val origin = structureAxis ?: return null
        val structureId = structure ?: return null
        val template = level.structureManager.getOrCreate(structureId)
        val size = template.size
        val extra = padding.coerceAtLeast(0)
        val farCorner = origin.offset(size.x - 1, size.y - 1, size.z - 1)

        return StructureClearBounds(
            BlockPos(
                minOf(origin.x, farCorner.x) - extra,
                minOf(origin.y, farCorner.y) - extra,
                minOf(origin.z, farCorner.z) - extra
            ),
            BlockPos(
                maxOf(origin.x, farCorner.x) + extra,
                maxOf(origin.y, farCorner.y) + extra,
                maxOf(origin.z, farCorner.z) + extra
            )
        )
    }

    companion object {
        @JvmStatic
        fun create(): Builder = Builder()
    }

    @Suppress("unused")
    class Builder {
        var structureValue: ResourceLocation? = null
        var structureAxisValue: BlockPos? = null
        var structureDimensionValue: ResourceKey<Level>? = null
        var playerInputAxisValue: BlockPos? = null
        var seasonsValue: Set<Season>? = null
        var subSeasonValue: Season.SubSeason? = null
        var dayValue: Int? = null
        var dimensionNameKeyValue: String? = null
        var festivalNameKeyValue: String? = null
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

        fun dimensionName(dimensionNameKey: String?): Builder = apply {
            dimensionNameKeyValue = dimensionNameKey
        }

        fun dimensionNameKey(dimensionNameKey: String?): Builder = dimensionName(dimensionNameKey)

        fun festivalName(festivalNameKey: String?): Builder = apply {
            festivalNameKeyValue = festivalNameKey
        }

        fun festivalNameKey(festivalNameKey: String?): Builder = festivalName(festivalNameKey)

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

data class StructureClearBounds(val start: BlockPos, val end: BlockPos)
