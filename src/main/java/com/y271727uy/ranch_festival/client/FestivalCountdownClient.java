package com.y271727uy.ranch_festival.client;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import com.y271727uy.ranch_festival.dimension.DimensionAccessController;
import com.y271727uy.ranch_festival.season.SeasonAccessChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FestivalCountdownClient {
    private static final String KEY_FESTIVAL_COUNTDOWN = "ranch_festival.message.festival_countdown";
    private static final long FESTIVAL_HINT_DURATION_TICKS = 20L * 10L;

    private static int remainingSeconds = -1;
    private static long festivalHintVisibleUntil = -1L;
    private static String festivalHintKey;
    private static String lastFestivalSoundKey;
    private static Component festivalHintMessage;

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

        updateFestivalHint(minecraft);
        updateCountdown(minecraft);
    }

    private static void updateFestivalHint(Minecraft minecraft) {
        Level level = minecraft.level;
        if (level == null) {
            clearFestivalHint();
            return;
        }

        FestivalTodayHintState hint = FestivalTodayHint.resolveTodayFestivalHint(level);
        if (hint == null) {
            return;
        }

        long now = level.getGameTime();
        if (!hint.getKey().equals(festivalHintKey)) {
            festivalHintKey = hint.getKey();
            festivalHintMessage = hint.getMessage();
            festivalHintVisibleUntil = now + FESTIVAL_HINT_DURATION_TICKS;
            if (!hint.getKey().equals(lastFestivalSoundKey) && minecraft.player != null) {
                lastFestivalSoundKey = hint.getKey();
                minecraft.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
            return;
        }

        if (festivalHintMessage == null) {
            festivalHintMessage = hint.getMessage();
        }
    }

    private static void updateCountdown(Minecraft minecraft) {
        Level level = minecraft.level;
        if (level == null || !DimensionAccessController.isManagedDimension(level.dimension())) {
            resetCountdown();
            return;
        }

        int remainingTicks = SeasonAccessChecker.getTicksUntilDayEnds(level);
        if (remainingTicks <= 0 || remainingTicks > 1200) {
            resetCountdown();
            return;
        }

        remainingSeconds = Math.max(1, (remainingTicks + 19) / 20);
    }

    @SuppressWarnings("unused")
    static int getRemainingSeconds() {
        return remainingSeconds;
    }

    @SuppressWarnings("unused")
    static String getCountdownKey() {
        return KEY_FESTIVAL_COUNTDOWN;
    }

    @SuppressWarnings("unused")
    static boolean isFestivalHintVisible() {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        return level != null && festivalHintMessage != null && level.getGameTime() < festivalHintVisibleUntil;
    }

    @SuppressWarnings("unused")
    static Component getFestivalHintMessage() {
        return festivalHintMessage;
    }

    private static void reset() {
        resetCountdown();
        clearFestivalHint();
    }

    private static void resetCountdown() {
        remainingSeconds = -1;
    }

    private static void clearFestivalHint() {
        festivalHintVisibleUntil = -1L;
        festivalHintKey = null;
        festivalHintMessage = null;
    }
}
