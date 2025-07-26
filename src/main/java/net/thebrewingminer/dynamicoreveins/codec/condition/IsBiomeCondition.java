package net.thebrewingminer.dynamicoreveins.codec.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import static net.minecraft.world.biome.source.BiomeCoords.fromBlock;

// Adapted from Lithostitched by ApolloUnknownDev
// Original source: https://github.com/Apollounknowndev/lithostitched
// Licensed under the MIT License
public record IsBiomeCondition(RegistryEntryList<Biome> biomes) implements IVeinCondition {
    // Build codec with Mojang's biome REGISTRY_ENTRY_LIST_CODEC.
    public static final Codec<IsBiomeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.REGISTRY_ENTRY_LIST_CODEC.fieldOf("biomes").forGetter(IsBiomeCondition::biomes)
    ).apply(instance, IsBiomeCondition::new));

    @Override
    public boolean test(Context context) {
        // Get necessary info from the worldgen context.
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        MultiNoiseUtil.MultiNoiseSampler sampler = context.noiseConfig().getMultiNoiseSampler();
        int x = context.pos().getX();
        int y = context.pos().getY();
        int z = context.pos().getZ();

        // Test if the context's position is within or on the edge of the expected biome(s).
        RegistryEntry<Biome> currBiome = chunkGenerator.getBiomeSource().getBiome(fromBlock(x), fromBlock(y), fromBlock(z), sampler);
        return (this.biomes.contains(currBiome));
    }

    @Override
    public String type() {
        // String identifier.
        return "dynamic_veins:biome";
    }
}