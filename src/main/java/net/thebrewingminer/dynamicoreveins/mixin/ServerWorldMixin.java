package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.thebrewingminer.dynamicoreveins.accessor.IDimensionAware;
import net.thebrewingminer.dynamicoreveins.accessor.WorldSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstruct(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci){
        ChunkGenerator chunkGenerator = dimensionOptions.chunkGenerator();  // DimensionOptions provides the chunk generator.
        ((IDimensionAware)chunkGenerator).setDimension(worldKey);           // Cache the dimension registry key into the chunk generator.
        WorldSeedHolder.setSeed(seed);                                      // Save the (not yet transformed) seed for later.
    }
}