package net.thebrewingminer.dynamicoreveins.helper;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;

import java.util.HashMap;
import java.util.Map;

public class LegacyNoiseDensityFunctionVisitor implements DensityFunction.DensityFunctionVisitor{
    private final Map<DensityFunction, DensityFunction> cache = new HashMap<>();
    private final long seed;
    private final boolean usesLegacyRandom;
    private final NoiseConfig noiseConfig;
    final RandomSplitter randomSplitter;

    public LegacyNoiseDensityFunctionVisitor(IVeinCondition.Context context){
        this.seed = context.seed();
        this.usesLegacyRandom = context.usesLegacyRandom();
        this.noiseConfig = context.noiseConfig();
        this.randomSplitter = context.randomSplitter();
    }

    private CheckedRandom createRandom(long noiseSeed){
        return new CheckedRandom(seed + noiseSeed);
    }

    public DensityFunction.Noise apply(DensityFunction.Noise noiseDensityFunction) {
        RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noiseDensityFunction.noiseData();
        DoublePerlinNoiseSampler doublePerlinNoiseSampler;
        if (this.usesLegacyRandom) {
            if (registryEntry.matchesKey(NoiseParametersKeys.TEMPERATURE)) {
                doublePerlinNoiseSampler = DoublePerlinNoiseSampler.createLegacy(this.createRandom(0L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, new double[]{1.0}));
                return new DensityFunction.Noise(registryEntry, doublePerlinNoiseSampler);
            }

            if (registryEntry.matchesKey(NoiseParametersKeys.VEGETATION)) {
                doublePerlinNoiseSampler = DoublePerlinNoiseSampler.createLegacy(this.createRandom(1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, new double[]{1.0}));
                return new DensityFunction.Noise(registryEntry, doublePerlinNoiseSampler);
            }

            if (registryEntry.matchesKey(NoiseParametersKeys.OFFSET)) {
                doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(this.randomSplitter.split(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0, new double[0]));
                return new DensityFunction.Noise(registryEntry, doublePerlinNoiseSampler);
            }
        }

        doublePerlinNoiseSampler = this.noiseConfig.getOrCreateSampler(registryEntry.getKey().orElseThrow());
        return new DensityFunction.Noise(registryEntry, doublePerlinNoiseSampler);
    }

    private DensityFunction applyNotCached(DensityFunction densityFunction) {
        if (densityFunction instanceof InterpolatedNoiseSampler interpolatedNoiseSampler) {
            Random random = this.usesLegacyRandom ? this.createRandom(0L) : this.randomSplitter.split(new Identifier("terrain"));
            return interpolatedNoiseSampler.copyWithRandom(random);
        } else {
            return ((densityFunction instanceof DensityFunctionTypes.EndIslands) ? new DensityFunctionTypes.EndIslands(seed) : densityFunction);
        }
    }

    @Override
    public DensityFunction apply(DensityFunction densityFunction) {
        return this.cache.computeIfAbsent(densityFunction, this::applyNotCached);
    }
}