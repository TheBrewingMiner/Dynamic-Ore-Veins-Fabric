package net.thebrewingminer.dynamicoreveins.registry;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.thebrewingminer.dynamicoreveins.codec.DebugSettings;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.VeinSettingsConfig;

import java.util.Map;

public class OreVeinRegistryHolder {
    private static DynamicRegistryManager.Immutable registryManager;
    public static final RegistryKey<Registry<OreVeinConfig>> VEIN_REGISTRY = OreVeinRegistries.ORE_VEIN_REGISTRY;
    public static final RegistryKey<Registry<VeinSettingsConfig>> CONFIG_REGISTRY = OreVeinRegistries.VEIN_SETTINGS_REGISTRY;

    public static void init(DynamicRegistryManager.Immutable dynamicRegistryManager){
        registryManager = dynamicRegistryManager;
    }

    public static Registry<OreVeinConfig> getVeinRegistry(){
        if (registryManager == null) throw new IllegalStateException("The registry manager was not initialized!");
        return registryManager.get(VEIN_REGISTRY);
    }

    public static Registry<VeinSettingsConfig> getConfigRegistry(){
        if (registryManager == null) throw new IllegalStateException("The registry manager was not initialized!");
        return registryManager.get(CONFIG_REGISTRY);
    }

    public static VeinSettingsConfig getActiveConfig(){
        return getConfigRegistry().getEntrySet().stream()
            .findFirst()
            .map(Map.Entry::getValue)
            .orElseThrow(() -> new IllegalStateException("No VeinSettingsConfig present in registry!"));
    }

    public static DebugSettings getActiveDebugSettings(){
        return getActiveConfig().debugSettings();
    }
}