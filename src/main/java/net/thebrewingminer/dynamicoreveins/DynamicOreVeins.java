package net.thebrewingminer.dynamicoreveins;

import net.fabricmc.api.ModInitializer;
import net.thebrewingminer.dynamicoreveins.event.LifecycleServerEvent;
import net.thebrewingminer.dynamicoreveins.registry.OreVeinRegistries;

public class DynamicOreVeins implements ModInitializer {

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		OreVeinRegistries.register();
		LifecycleServerEvent.registerEvents();
	}
}
