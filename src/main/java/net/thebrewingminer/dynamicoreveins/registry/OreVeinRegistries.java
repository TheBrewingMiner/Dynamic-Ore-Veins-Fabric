package net.thebrewingminer.dynamicoreveins.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.VeinSettingsConfig;

public class OreVeinRegistries {
    public static final RegistryKey<Registry<OreVeinConfig>> ORE_VEIN_REGISTRY = RegistryKey.ofRegistry(Identifier.tryParse("worldgen", "ore_vein"));
    public static final RegistryKey<Registry<VeinSettingsConfig>> VEIN_SETTINGS_REGISTRY = RegistryKey.ofRegistry(Identifier.tryParse("config", "vein_settings"));

    public static void register(){
        DynamicRegistries.register(ORE_VEIN_REGISTRY, OreVeinConfig.CODEC);
        DynamicRegistries.register(VEIN_SETTINGS_REGISTRY, VeinSettingsConfig.CODEC);
    }
}