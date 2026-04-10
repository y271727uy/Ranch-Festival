package com.y271727uy.ranch_festival.dimension;

import com.y271727uy.ranch_festival.season.FestivalStayState;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DimensionTeleporter {
    private static final String KEY_DIMENSION_CONFIG_INCOMPLETE = "ranch_festival.message.dimension_config_incomplete";
    private static final String KEY_SERVER_UNAVAILABLE = "ranch_festival.message.server_unavailable";
    private static final String KEY_TARGET_DIMENSION_UNLOADED = "ranch_festival.message.target_dimension_unloaded";
    private static final String KEY_OVERWORLD_UNLOADED = "ranch_festival.message.overworld_unloaded";

    private DimensionTeleporter() {
    }

    public static InteractionResultHolder<ItemStack> teleport(ServerPlayer serverPlayer, ItemStack itemStack, DimensionDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        if (definition.getPlayerInputAxis() == null || definition.getStructureDimension() == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable(KEY_DIMENSION_CONFIG_INCOMPLETE),
                    false
            );
            return InteractionResultHolder.fail(itemStack);
        }

        if (serverPlayer.getServer() == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable(KEY_SERVER_UNAVAILABLE),
                    false
            );
            return InteractionResultHolder.fail(itemStack);
        }

        ServerLevel targetLevel = serverPlayer.getServer().getLevel(definition.getStructureDimension());
        if (targetLevel == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable(KEY_TARGET_DIMENSION_UNLOADED, definition.getStructureDimension().location()),
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

    public static void returnToOverworldSpawn(ServerPlayer serverPlayer, Component message) {
        if (serverPlayer.getServer() == null) {
            serverPlayer.displayClientMessage(message, false);
            return;
        }

        ServerLevel overworld = serverPlayer.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable(KEY_OVERWORLD_UNLOADED),
                    false
            );
            return;
        }

        BlockPos spawn = overworld.getSharedSpawnPos();
        serverPlayer.teleportTo(
                overworld,
                spawn.getX() + 0.5D,
                spawn.getY() + 1.0D,
                spawn.getZ() + 0.5D,
                serverPlayer.getYRot(),
                serverPlayer.getXRot()
        );

        FestivalStayState.clear(serverPlayer);
        serverPlayer.displayClientMessage(message, false);
    }
}
