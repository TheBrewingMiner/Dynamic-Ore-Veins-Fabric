package net.thebrewingminer.dynamicoreveins.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.VeinSettingsConfig;
import net.thebrewingminer.dynamicoreveins.codec.condition.DensityFunctionThreshold;
import net.thebrewingminer.dynamicoreveins.helper.ExtractHeightConditions;
import net.thebrewingminer.dynamicoreveins.helper.PrepareList;
import net.thebrewingminer.dynamicoreveins.registry.OreVeinRegistryHolder;

public class ModServerLifecycleEvents {
  public static void onServerStarting(){
      ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
          // Get the registry manager and store it for use in OreVeinRegistryHolder.
          DynamicRegistryManager.Immutable registryManager = server.getRegistryManager();
          OreVeinRegistryHolder.init(registryManager);

          System.out.println("[DOV] Registry initialized on server start.");

          // Verify we got it and see how many configs are loaded.
          Registry<OreVeinConfig> veinRegistry = OreVeinRegistryHolder.getVeinRegistry();
          System.out.println("[DOV] Ore Vein Registry size: " + veinRegistry.size());

          Registry<VeinSettingsConfig> configRegistry = OreVeinRegistryHolder.getConfigRegistry();
          if ((configRegistry.size() == 1)) {
              System.out.println("[DOV] 1 config loaded!");
          } else if (configRegistry.size() == 0){
              throw new IllegalStateException("Missing vein settings file in ~config/vein_settings/*! Where'd it go?");
          } else {
              System.err.println("[DOV] More than one config loaded. Are you sure?");
          }
      });
  }

  public static void onServerStopped(){
      ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
          // Clears all caches on world close.

          DensityFunctionThreshold.clearCache();
          OreVeinRegistryHolder.getConfigRegistry().forEach(VeinSettingsConfig::clearCache);
          System.out.println("[DOV] Cleared density function caches.");

          ExtractHeightConditions.clearCache();
          System.out.println("[DOV] Cleared HeightRangeCondition lists cache.");

          PrepareList.clearCache();
          System.out.println("[DOV] Cleared shuffled vein lists cache.");
      });
  }

  public static void registerEvents(){
      onServerStarting();
      onServerStopped();
  }
}