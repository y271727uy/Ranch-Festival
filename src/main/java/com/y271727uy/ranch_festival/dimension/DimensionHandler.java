package com.y271727uy.ranch_festival.dimension;

import com.y271727uy.ranch_festival.season.SeasonAccessChecker;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DimensionHandler {
    private DimensionHandler() {
    }

    public static InteractionResultHolder<ItemStack> handleUse(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Object maybeDefinition = DimensionRegistry.findByInvitation(itemStack);

        if (maybeDefinition == null) {
            player.displayClientMessage(Component.literal("✗ 这个物品还没有绑定可进入的维度配置"), false);
            return InteractionResultHolder.fail(itemStack);
        }

        return handleUse(world, player, hand, (DimensionDefinition) maybeDefinition);
    }

    public static InteractionResultHolder<ItemStack> handleUse(Level world, Player player, InteractionHand hand, DimensionDefinition definition) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        var seasonInfo = SeasonAccessChecker.getSeasonState(world);
        Season season = seasonInfo.getSeason();
        Season.SubSeason subSeason = seasonInfo.getSubSeason();
        int day = SeasonAccessChecker.getDayOfSeason(world);
        boolean accessAllowed = SeasonAccessChecker.isDimensionAccessAllowed(world, definition);

        displaySeasonInfo(player, season, subSeason, day, accessAllowed, definition);

        if (!accessAllowed) {
            player.displayClientMessage(
                    Component.literal("✗ 未满足当前节日配置，无法进入 " + describeDimension(definition)),
                    false
            );
            return InteractionResultHolder.success(itemStack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(itemStack);
        }

        return DimensionTeleporter.teleport(serverPlayer, itemStack, definition);
    }

    private static void displaySeasonInfo(Player player, Season season, Season.SubSeason subSeason, int day, boolean accessAllowed, DimensionDefinition definition) {
        player.displayClientMessage(Component.literal("=== 季节信息 ==="), false);
        player.displayClientMessage(Component.literal("季节: " + season.name()), false);
        player.displayClientMessage(Component.literal("子季节: " + subSeason.name()), false);
        player.displayClientMessage(Component.literal("当前季节第几天: " + day), false);
        player.displayClientMessage(
                Component.literal("访问状态: " + (accessAllowed ? "✓ 允许进入 " + describeDimension(definition) : "✗ 未满足当前节日配置")),
                false
        );
    }

    private static String describeDimension(DimensionDefinition definition) {
        return definition.getStructureDimension() == null
                ? "<unknown>"
                : definition.getStructureDimension().location().toString();
    }
}


