package com.y271727uy.ranch_festival.client;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionAccessController;
import com.y271727uy.ranch_festival.season.SeasonAccessChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FestivalCountdownClient {
    private static final String KEY_FESTIVAL_COUNTDOWN = "ranch_festival.message.festival_countdown";

    private static int lastDisplayedSecond = -1;

    private FestivalCountdownClient() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            reset();
            return;
        }

        if (!DimensionAccessController.isManagedDimension(minecraft.level.dimension())) {
            reset();
            return;
        }

        int remainingTicks = SeasonAccessChecker.getTicksUntilDayEnds(minecraft.level);
        if (remainingTicks <= 0 || remainingTicks > 1200) {
            reset();
            return;
        }

        int remainingSeconds = (remainingTicks + 19) / 20;
        if (remainingSeconds != lastDisplayedSecond) {
            lastDisplayedSecond = remainingSeconds;
            minecraft.player.displayClientMessage(Component.translatable(KEY_FESTIVAL_COUNTDOWN, remainingSeconds), true);
        }
    }

    private static void reset() {
        Minecraft minecraft = Minecraft.getInstance();
        if (lastDisplayedSecond != -1 && minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.empty(), true);
        }

        lastDisplayedSecond = -1;
    }
}


