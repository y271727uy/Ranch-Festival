package com.y271727uy.ranch_festival.event.item;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionDefinition;
import com.y271727uy.ranch_festival.dimension.DimensionHandler;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class InvitationUseHandler {
    private InvitationUseHandler() {
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        DimensionDefinition definition = DimensionDefinition.findByInvitation(stack);
        if (definition == null) {
            return;
        }

        InteractionResultHolder<ItemStack> result = DimensionHandler.handleUse(event.getLevel(), player, event.getHand(), definition);
        event.setCanceled(true);
        event.setCancellationResult(result.getResult());
    }
}


