package com.y271727uy.ranch_festival.all;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItem {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RanchFestivalMod.MODID);

	public static final RegistryObject<Item> TEXT_INVITATION = ITEMS.register("text_invitation", () -> new Item(new Item.Properties().stacksTo(64)));

	public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
		ITEMS.register(eventBus);
	}

	private ModItem() {
	}
}
