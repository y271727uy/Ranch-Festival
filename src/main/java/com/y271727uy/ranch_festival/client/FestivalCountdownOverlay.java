package com.y271727uy.ranch_festival.client;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RanchFestivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FestivalCountdownOverlay {
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int TEXT_Y_OFFSET = 59;
    private static final int EXTRA_LINE_OFFSET = 12;

    private FestivalCountdownOverlay() {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "festival_countdown", countdownOverlay());
    }

    private static IGuiOverlay countdownOverlay() {
        return (gui, graphics, partialTick, width, height) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
                return;
            }

            int baseY = height - TEXT_Y_OFFSET;
            if (FestivalCountdownClient.isFestivalHintVisible()) {
                Component todayFestivalMessage = FestivalCountdownClient.getFestivalHintMessage();
                if (todayFestivalMessage != null) {
                    drawCenteredMessage(graphics, minecraft.font, todayFestivalMessage, width, baseY - EXTRA_LINE_OFFSET);
                }
            }

            int remainingSeconds = FestivalCountdownClient.getRemainingSeconds();
            if (remainingSeconds <= 0) {
                return;
            }

            Component message = Component.translatable(FestivalCountdownClient.getCountdownKey(), remainingSeconds);
            drawCenteredMessage(graphics, minecraft.font, message, width, baseY);
        };
    }

    private static void drawCenteredMessage(GuiGraphics graphics, Font font, Component message, int width, int y) {
        int x = (width - font.width(message)) / 2;
        graphics.drawString(font, message, x, y, TEXT_COLOR, true);
    }
}
