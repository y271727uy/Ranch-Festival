package com.y271727uy.ranch_festival.dimension

import com.y271727uy.ranch_festival.RanchFestivalMod
import com.y271727uy.ranch_festival.all.ModItem
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import sereneseasons.api.season.Season

@JvmField
val TEST: DimensionDefinition = dimensionDefinition {
    structure(null as ResourceLocation?)
    structureAxis(0, 64, 0)
    structureDimension(
        ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(RanchFestivalMod.MODID, "test")
        )
    )
    playerInputAxis(0, 64, 0)
    seasons(Season.SPRING)
    subSeason(null)
    day(2)
    survivalPlayerDestroy(false)
    autoReset(true)
    mobSpawn(false)
    invitation(ModItem.TEXT_INVITATION)
}.register()

fun bootstrap() {
    // 触发文件级初始化，确保静态定义完成注册。
}



