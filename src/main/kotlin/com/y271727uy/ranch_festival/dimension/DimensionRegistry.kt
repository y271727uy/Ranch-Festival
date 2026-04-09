package com.y271727uy.ranch_festival.dimension

import java.util.Collections
import java.util.LinkedHashSet
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object DimensionRegistry {
    private val definitions: MutableSet<DimensionDefinition> = LinkedHashSet()

    @JvmStatic
    fun register(definition: DimensionDefinition): DimensionDefinition {
        definitions.add(definition)
        return definition
    }

    @JvmStatic
    fun findByDimension(dimension: ResourceKey<Level>?): DimensionDefinition? {
        if (dimension == null) {
            return null
        }

        return definitions.firstOrNull { it.structureDimension == dimension }
    }

    @JvmStatic
    fun findByInvitation(itemStack: ItemStack?): DimensionDefinition? {
        if (itemStack == null || itemStack.isEmpty) {
            return null
        }

        return definitions.firstOrNull { it.matchesInvitation(itemStack.item) }
    }

    @JvmStatic
    @Suppress("unused")
    fun all(): Set<DimensionDefinition> {
        return Collections.unmodifiableSet(LinkedHashSet(definitions))
    }
}


