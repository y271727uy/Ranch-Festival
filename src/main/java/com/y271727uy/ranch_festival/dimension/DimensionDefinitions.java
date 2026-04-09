package com.y271727uy.ranch_festival.dimension;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.all.ModItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sereneseasons.api.season.Season;

public final class DimensionDefinitions {
    public static final DimensionDefinition TEST = DimensionDefinition.create()
            .Structure((ResourceLocation) null)
            .StructureAxis(0, 64, 0)
            .StructureDimension(ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(RanchFestivalMod.MODID, "test")
            ))
            .PlayerInputAxis(0, 64, 0)
            .Seasons(Season.SPRING)
            .SubSeason(null)
            .Day(2)
            .SurvivalPlayerDestory(false)
            .AutoReset(true)
            .MobSpawn(false)
            .invitation(ModItem.TEXT_INVITATION)
            .register();

    private DimensionDefinitions() {
    }

    public static void bootstrap() {
        // 触发类初始化，确保静态定义完成注册。
    }
}

