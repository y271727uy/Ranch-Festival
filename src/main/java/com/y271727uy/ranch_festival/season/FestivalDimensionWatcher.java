package com.y271727uy.ranch_festival.season;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.Config;
import com.y271727uy.ranch_festival.dimension.DimensionAccessController;
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

        syncOrEnforce(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncOrEnforce(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncOrEnforce(serverPlayer);
        }
    }

    private static void syncOrEnforce(ServerPlayer serverPlayer) {
        if (!Config.enableTheDimensionSeasonLock) {
            FestivalStayState.clear(serverPlayer);
            return;
        }

        var currentLevel = serverPlayer.serverLevel();
        int currentDay = SeasonAccessChecker.getDayOfSeason(currentLevel);
        if (currentDay <= 0) {
            return;
        }

        boolean managedDimension = DimensionAccessController.isManagedDimension(currentLevel.dimension());
        Integer entryDay = FestivalStayState.getEntryDay(serverPlayer);

        if (!managedDimension) {
            if (entryDay != null && FestivalStayState.isExpired(serverPlayer, currentDay)) {
                FestivalStayState.clear(serverPlayer);
            }
            return;
        }

        if (entryDay == null) {
            FestivalStayState.recordEntryDay(serverPlayer, currentDay);
            return;
        }

        if (!FestivalStayState.isExpired(serverPlayer, currentDay)) {
            return;
        }

        FestivalStayState.clear(serverPlayer);
        RanchFestivalMod.LOGGER.info(
                "Player {} stayed in festival dimension past day {}, returning to overworld spawn",
                serverPlayer.getScoreboardName(),
                entryDay
        );

        DimensionTeleporter.returnToOverworldSpawn(
                serverPlayer,
                Component.translatable("ranch_festival.message.returned_to_overworld_spawn")
        );
    }
}


