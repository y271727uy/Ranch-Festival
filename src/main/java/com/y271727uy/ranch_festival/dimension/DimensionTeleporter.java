package com.y271727uy.ranch_festival.dimension;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DimensionTeleporter {
    private DimensionTeleporter() {
    }

    public static InteractionResultHolder<ItemStack> teleport(ServerPlayer serverPlayer, ItemStack itemStack, DimensionDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        if (definition.getPlayerInputAxis() == null || definition.getStructureDimension() == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("✗ 该维度配置缺少目标坐标或目标维度"),
                    false
            );
            return InteractionResultHolder.fail(itemStack);
        }

        if (serverPlayer.getServer() == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("服务器实例不可用，无法传送"),
                    false
            );
            return InteractionResultHolder.fail(itemStack);
        }

        ServerLevel targetLevel = serverPlayer.getServer().getLevel(definition.getStructureDimension());
        if (targetLevel == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("维度 " + definition.getStructureDimension().location() + " 未加载"),
                    false
            );
            return InteractionResultHolder.fail(itemStack);
        }

        serverPlayer.teleportTo(
                targetLevel,
                definition.getPlayerInputAxis().getX(),
                definition.getPlayerInputAxis().getY(),
                definition.getPlayerInputAxis().getZ(),
                serverPlayer.getYRot(),
                serverPlayer.getXRot()
        );
        return InteractionResultHolder.success(itemStack);
    }
}

