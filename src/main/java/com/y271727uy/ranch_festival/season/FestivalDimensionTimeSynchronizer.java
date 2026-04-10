package com.y271727uy.ranch_festival.season;

import com.y271727uy.ranch_festival.Config;
import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import com.y271727uy.ranch_festival.dimension.DimensionRegistry;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FestivalDimensionTimeSynchronizer {
    private FestivalDimensionTimeSynchronizer() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Config.enableTheDimensionSeasonLock) {
            return;
        }

        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }

        long overworldDayTime = overworld.getDayTime();
        for (DimensionDefinition definition : DimensionRegistry.all()) {
            if (definition.getStructureDimension() == null || Level.OVERWORLD.equals(definition.getStructureDimension())) {
                continue;
            }

            ServerLevel targetLevel = server.getLevel(definition.getStructureDimension());
            if (targetLevel == null) {
                continue;
            }

            if (targetLevel.getDayTime() != overworldDayTime) {
                targetLevel.setDayTime(overworldDayTime);
            }
        }
    }
}

