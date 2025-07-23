package net.thebrewingminer.dynamicoreveins.codec.condition;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

public interface IVeinCondition {
    // Implementors of conditions must have a test method and a string type identifier.
    boolean test(Context context);
    String type();

    // Defines the "worldgen context" that carries the necessary information
    // to test at a position in the world.
    interface Context {
        BlockPos pos();
        HeightLimitView heightLimitViewer();
        ChunkGenerator chunkGenerator();
        long seed();
        boolean usesLegacyRandom();
        NoiseConfig noiseConfig();
        RandomSplitter randomSplitter();
        RegistryKey<World> dimension();
    }
}