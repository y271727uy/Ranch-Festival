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
            player.displayClientMessage(Component.translatable("ranch_festival.message.invitation_unbound"), false);
            return InteractionResultHolder.fail(itemStack);
        }

        DimensionDefinition definition = (DimensionDefinition) maybeDefinition;
        return handleUse(world, player, hand, definition);
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
                    Component.translatable("ranch_festival.message.dimension_season_locked", describeDimension(definition)),
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
        player.displayClientMessage(Component.translatable("ranch_festival.message.season_info.title"), false);
        player.displayClientMessage(Component.translatable("ranch_festival.message.season_info.season", Component.translatable(seasonKey(season))), false);
        player.displayClientMessage(Component.translatable("ranch_festival.message.season_info.subseason", Component.translatable(subSeasonKey(subSeason))), false);
        player.displayClientMessage(Component.translatable("ranch_festival.message.season_info.day", day), false);
        player.displayClientMessage(
                Component.translatable(
                        accessAllowed
                                ? "ranch_festival.message.season_info.access_allowed"
                                : "ranch_festival.message.season_info.access_denied",
                        describeDimension(definition)
                ),
                false
        );
    }

    private static String describeDimension(DimensionDefinition definition) {
        return definition.getStructureDimension() == null
                ? Component.translatable("ranch_festival.message.unknown_dimension").getString()
                : definition.getStructureDimension().location().toString();
    }

    private static String seasonKey(Season season) {
        return switch (season) {
            case SPRING -> "ranch_festival.season.spring";
            case SUMMER -> "ranch_festival.season.summer";
            case AUTUMN -> "ranch_festival.season.autumn";
            case WINTER -> "ranch_festival.season.winter";
            default -> "ranch_festival.season.unknown";
        };
    }

    private static String subSeasonKey(Season.SubSeason subSeason) {
        return switch (subSeason) {
            case EARLY_SPRING -> "ranch_festival.subseason.early_spring";
            case MID_SPRING -> "ranch_festival.subseason.mid_spring";
            case LATE_SPRING -> "ranch_festival.subseason.late_spring";
            case EARLY_SUMMER -> "ranch_festival.subseason.early_summer";
            case MID_SUMMER -> "ranch_festival.subseason.mid_summer";
            case LATE_SUMMER -> "ranch_festival.subseason.late_summer";
            case EARLY_AUTUMN -> "ranch_festival.subseason.early_autumn";
            case MID_AUTUMN -> "ranch_festival.subseason.mid_autumn";
            case LATE_AUTUMN -> "ranch_festival.subseason.late_autumn";
            case EARLY_WINTER -> "ranch_festival.subseason.early_winter";
            case MID_WINTER -> "ranch_festival.subseason.mid_winter";
            case LATE_WINTER -> "ranch_festival.subseason.late_winter";
        };
    }
}


