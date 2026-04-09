package com.y271727uy.ranch_festival.event.player;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PlayerBlockBreakHandler {
    private PlayerBlockBreakHandler() {
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        if (player.isCreative()) {
            return;
        }

        DimensionDefinition definition = DimensionDefinition.findByDimension(player.serverLevel().dimension());
        if (definition == null || !Boolean.FALSE.equals(definition.getSurvivalPlayerDestroy())) {
            return;
        }

        event.setCanceled(true);
        player.displayClientMessage(net.minecraft.network.chat.Component.literal("✗ 该维度禁止生存玩家破坏方块"), true);
    }
}




