package com.y271727uy.ranch_festival;

import com.mojang.logging.LogUtils;
import com.y271727uy.ranch_festival.all.ModItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RanchFestivalMod.MODID)
public class RanchFestivalMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "ranch_festival";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public RanchFestivalMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        LOGGER.info("Initializing mod {}", MODID);
        
        // Register items
        ModItem.register(modEventBus);

        // Register config
        context.registerConfig(ModConfig.Type.COMMON, Config.SEASON_SPEC);
        
        // Register config event listeners
        modEventBus.register(Config.class);
    }

}