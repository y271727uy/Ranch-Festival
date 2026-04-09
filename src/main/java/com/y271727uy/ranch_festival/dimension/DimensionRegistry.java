package com.y271727uy.ranch_festival.dimension;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class DimensionRegistry {
    private static final Set<DimensionDefinition> DEFINITIONS = new LinkedHashSet<>();

    private DimensionRegistry() {
    }

    public static DimensionDefinition register(DimensionDefinition definition) {
        DEFINITIONS.add(definition);
        return definition;
    }

    public static DimensionDefinition findByDimension(ResourceKey<Level> dimension) {
        for (DimensionDefinition definition : DEFINITIONS) {
            if (definition != null && dimension != null && dimension.equals(definition.getStructureDimension())) {
                return definition;
            }
        }
        return null;
    }

    public static DimensionDefinition findByInvitation(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }

        for (DimensionDefinition definition : DEFINITIONS) {
            if (definition != null && definition.matchesInvitation(itemStack.getItem())) {
                return definition;
            }
        }
        return null;
    }

    public static Set<DimensionDefinition> all() {
        return Set.copyOf(DEFINITIONS);
    }
}
