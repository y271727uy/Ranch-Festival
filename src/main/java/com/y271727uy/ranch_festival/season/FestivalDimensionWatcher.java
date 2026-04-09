package com.y271727uy.ranch_festival.season;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import com.y271727uy.ranch_festival.dimension.DimensionRegistry;
import com.y271727uy.ranch_festival.dimension.DimensionTeleporter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FestivalDimensionWatcher {
    private FestivalDimensionWatcher() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if ((serverPlayer.tickCount & 19) != 0) {
            return;
        }

        enforceAccess(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            enforceAccess(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            enforceAccess(serverPlayer);
        }
    }

    private static void enforceAccess(ServerPlayer serverPlayer) {
        var currentLevel = serverPlayer.serverLevel();
        DimensionDefinition definition = DimensionRegistry.findByDimension(currentLevel.dimension());
        if (definition == null) {
            return;
        }

        if (SeasonAccessChecker.isDimensionAccessAllowed(currentLevel, definition)) {
            return;
        }

        RanchFestivalMod.LOGGER.info(
                "Player {} no longer matches season lock for {}, returning to overworld spawn",
                serverPlayer.getScoreboardName(),
                definition.getStructureDimension() == null ? "<unknown>" : definition.getStructureDimension().location()
        );

        DimensionTeleporter.returnToOverworldSpawn(
                serverPlayer,
                Component.literal("当前季节已变化，已将你传回主世界。")
        );
    }
}


