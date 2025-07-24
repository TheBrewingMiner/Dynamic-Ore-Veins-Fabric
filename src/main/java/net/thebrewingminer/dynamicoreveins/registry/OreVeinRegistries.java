package net.thebrewingminer.dynamicoreveins.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;

public class OreVeinRegistries {
    public static final RegistryKey<Registry<OreVeinConfig>> ORE_VEIN_REGISTRY = RegistryKey.ofRegistry(new Identifier("worldgen", "ore_vein"));
}