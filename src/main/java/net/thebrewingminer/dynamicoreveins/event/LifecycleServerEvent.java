package net.thebrewingminer.dynamicoreveins.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.condition.DensityFunctionThreshold;
import net.thebrewingminer.dynamicoreveins.registry.OreVeinRegistryHolder;

public class LifecycleServerEvent {
  public static void onServerStarting(){
      ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
          // Get the registry manager and store it for use in OreVeinRegistryHolder.
          if (!OreVeinRegistryHolder.isInitialized()){
              DynamicRegistryManager.Immutable registryManager = server.getRegistryManager();
              OreVeinRegistryHolder.init(registryManager);

              System.out.println("[DOV] Registry initialized on server start.");

              // Verify we got it and see how many configs are loaded.
              Registry<OreVeinConfig> veinRegistry = OreVeinRegistryHolder.getVeinRegistry();
              System.out.println("[DOV] Ore Vein Registry size: " + veinRegistry.size());
          }
      });
  }

  public static void onServerStopped(){
      ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
          DensityFunctionThreshold.clearCache();

      });
  }

  public static void registerEvents(){
      onServerStarting();
      onServerStopped();
  }
}