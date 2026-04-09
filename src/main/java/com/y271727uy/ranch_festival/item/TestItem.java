package com.y271727uy.ranch_festival.item;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.y271727uy.ranch_festival.dimension.DimensionHandler;



@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TestItem extends Item {
    public TestItem() {
        // 使用与原版纸相同的属性
        super(new Item.Properties()
                .stacksTo(64)); // 堆叠大小64
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return DimensionHandler.handleUse(world, player, hand);
    }
}