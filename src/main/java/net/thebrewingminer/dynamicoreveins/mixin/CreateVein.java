package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.thebrewingminer.dynamicoreveins.accessor.ISettingsAccessor;
import net.thebrewingminer.dynamicoreveins.accessor.IWorldgenContext;
import net.thebrewingminer.dynamicoreveins.accessor.WorldSeedHolder;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkNoiseSampler.class)
public class CreateVein {

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/OreVeinSampler;create(Lnet/minecraft/world/gen/densityfunction/DensityFunction;Lnet/minecraft/world/gen/densityfunction/DensityFunction;Lnet/minecraft/world/gen/densityfunction/DensityFunction;Lnet/minecraft/util/math/random/RandomSplitter;)Lnet/minecraft/world/gen/chunk/ChunkNoiseSampler$BlockStateSampler;"
        )
    )
    private ChunkNoiseSampler.BlockStateSampler createVein(DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, RandomSplitter randomDeriver){
        return (functionContext) -> {
            BlockPos pos = new BlockPos(functionContext.blockX(), functionContext.blockY(), functionContext.blockZ());

            // Grab the cached chunk generator settings from this chunk.
            ChunkNoiseSampler noiseChunk = (ChunkNoiseSampler)(Object)this;
            ChunkGeneratorSettings chunkGenSettings = ((ISettingsAccessor)noiseChunk).getChunkGenSettings();

            // Grab the cached worldgen info from the chunk generator settings.
            IWorldgenContext wgContext = (IWorldgenContext)(Object)chunkGenSettings;
            ChunkGenerator chunkGenerator = wgContext.getChunkGenerator();
            HeightLimitView heightLimitView = wgContext.getHeightLimitView();
            RegistryKey<World> currDimension = wgContext.getDimension();

            // If missing required info, log once and return null
            if (currDimension == null || chunkGenerator == null || heightLimitView == null) {
                System.err.println("-------------------------------------------------------------------------------------------------------");
                System.err.println("[DOV] Warning: Worldgen context missing during BlockStateSampler evaluation. Skipping vein placement.");
                System.err.println("  -> NoiseChunk = " + noiseChunk);
                System.err.println("  -> generator = " + chunkGenerator);
                System.err.println("  -> dimensionKey = " + currDimension);
                System.err.println("  -> heightAccessor = " + heightLimitView);
                System.err.println("  -> Pos = " + pos);
                System.err.println("-------------------------------------------------------------------------------------------------------");
                return null;
            }

            // Grab the rest of the information from the chunk generator settings.
            boolean usesLegacyRandom = chunkGenSettings.usesLegacyRandom();
            NoiseConfig noiseConfig = ((ISettingsAccessor)noiseChunk).getNoiseConfig();

            long worldSeed = WorldSeedHolder.getSeed();     // Note that this is NOT the seed you see with /seed. This is the seed BEFORE the game hashes it.

            // Create a context object with all this info to process the JSON objects and test conditions.
            IVeinCondition.Context veinContext = new IVeinCondition.Context() {
                @Override public BlockPos pos() { return pos; }
                @Override public HeightLimitView heightLimitViewer() { return heightLimitView; }
                @Override public ChunkGenerator chunkGenerator() { return chunkGenerator; }
                @Override public long seed() { return worldSeed; }
                @Override public boolean usesLegacyRandom() { return usesLegacyRandom; }
                @Override public NoiseConfig noiseConfig() { return noiseConfig; }
                @Override public RandomSplitter randomSplitter() { return randomDeriver; }
                @Override public RegistryKey<World> dimension() { return currDimension; }
            };

            // Prepare the order in which to test veins.
            // Method call here.

            // Call vein sampler here.

        };
    }
}