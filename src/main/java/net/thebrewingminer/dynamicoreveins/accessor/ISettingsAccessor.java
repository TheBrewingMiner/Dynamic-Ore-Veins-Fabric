package net.thebrewingminer.dynamicoreveins.accessor;

import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.noise.NoiseConfig;

public interface ISettingsAccessor {
    // Implement means to retrieve the chunk generator settings.
    ChunkGeneratorSettings getChunkGenSettings();

    // Implement means to retrieve the Noise Config.
    NoiseConfig getNoiseConfig();
}