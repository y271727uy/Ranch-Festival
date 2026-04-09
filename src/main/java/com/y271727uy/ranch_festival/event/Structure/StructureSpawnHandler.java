package com.y271727uy.ranch_festival.event.Structure;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class StructureSpawnHandler {
    private StructureSpawnHandler() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        StructureGenerationController.handleLevelLoad(serverLevel);
    }
}


