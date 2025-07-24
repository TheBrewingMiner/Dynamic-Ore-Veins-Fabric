package net.thebrewingminer.dynamicoreveins.registry;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;

public class OreVeinRegistryHolder {
    private static DynamicRegistryManager.Immutable registryManager;
    public static final RegistryKey<Registry<OreVeinConfig>> VEIN_REGISTRY = OreVeinRegistries.ORE_VEIN_REGISTRY;

    public static void init(DynamicRegistryManager.Immutable dynamicRegistryManager){
        registryManager = dynamicRegistryManager;
    }

    public static Registry<OreVeinConfig> getVeinRegistry(){
        if (registryManager == null) throw new IllegalStateException("The registry manager was not initialized!");
        return registryManager.get(VEIN_REGISTRY);
    }

    public static boolean isInitialized(){
        return registryManager != null;
    }
}