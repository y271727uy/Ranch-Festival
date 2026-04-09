package com.y271727uy.ranch_festival.item;

import com.y271727uy.ranch_festival.dimension.DimensionHandler;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InvitationItem extends Item {
    public InvitationItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return DimensionHandler.handleUse(world, player, hand);
    }
}

