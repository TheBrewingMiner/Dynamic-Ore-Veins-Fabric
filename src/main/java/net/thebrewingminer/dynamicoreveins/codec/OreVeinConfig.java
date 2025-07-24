package net.thebrewingminer.dynamicoreveins.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record OreVeinConfig(BlockStateProvider ore, BlockStateProvider secondaryOre, float secondaryOreChance, BlockStateProvider fillerBlock){
    // The main config for worldgen devs to implement custom large ore veins.
    public static final Codec<OreVeinConfig> CODEC = RecordCodecBuilder.create(oreVeinConfigInstance -> oreVeinConfigInstance.group(
        ResourceKeyOrBlockState.CODEC.fieldOf("ore").forGetter(OreVeinConfig::ore),
        ResourceKeyOrBlockState.CODEC.fieldOf("secondary_ore").forGetter(OreVeinConfig::secondaryOre),
        Codec.floatRange(0.0f, 1.0f).fieldOf("secondary_ore_chance").forGetter(OreVeinConfig::secondaryOreChance),
        ResourceKeyOrBlockState.CODEC.fieldOf("filler_block").forGetter(OreVeinConfig::fillerBlock)
    ).apply(oreVeinConfigInstance, OreVeinConfig::new));
}