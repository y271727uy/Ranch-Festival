package com.y271727uy.ranch_festival.event.mob;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MobSpawnHandler {
    private MobSpawnHandler() {
    }

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        DimensionDefinition definition = DimensionDefinition.findByDimension(event.getLevel().getLevel().dimension());
        if (definition == null || Boolean.TRUE.equals(definition.getMobSpawn())) {
            return;
        }

        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }

        event.setSpawnCancelled(true);
    }
}




