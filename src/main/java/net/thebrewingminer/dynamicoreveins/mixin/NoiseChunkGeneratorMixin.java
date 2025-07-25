package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.thebrewingminer.dynamicoreveins.accessor.IDimensionAware;
import net.thebrewingminer.dynamicoreveins.accessor.ISettingsAccessor;
import net.thebrewingminer.dynamicoreveins.accessor.IWorldgenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(
        method = "Lnet/minecraft/world/gen/chunk/NoiseChunkGenerator;createChunkNoiseSampler(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/noise/NoiseConfig;)Lnet/minecraft/world/gen/chunk/ChunkNoiseSampler;",
        at = @At("RETURN")
    )
    private void attachWorldgenContext(Chunk chunk, StructureAccessor world, Blender blender, NoiseConfig noiseConfig, CallbackInfoReturnable<ChunkNoiseSampler> cir){
        // Grab the noise chunk being created by NoiseChunkGenerator.
        ChunkNoiseSampler noiseChunk = cir.getReturnValue();

        // Grab this instance of ChunkGenerator (this NoiseChunkGenerator calling createChunkNoiseSampler())
        ChunkGenerator chunkGenerator = (ChunkGenerator)(Object)this;

        // Grab the dimension key that was cached into the chunk generator (Stored by IDimensionAware, set in ServerWorldMixin).
        RegistryKey<World> dimension = ((IDimensionAware)chunkGenerator).getDimension();

        // Grab the height limit view from the chunk parameter.
        HeightLimitView heightLimitView = chunk.getHeightLimitView();

        // Grab the cached chunk generator settings stored by ISettingsAccessor in ChunkNoiseSampler.
        ChunkGeneratorSettings chunkGenSettings = ((ISettingsAccessor)noiseChunk).getChunkGenSettings();

        // Store the gathered info into IWorldgenContext in the chunk gen settings (Stored into IWorldgenContext in ChunkGeneratorSettings).
        IWorldgenContext wgContext = (IWorldgenContext)(Object)chunkGenSettings;

        wgContext.setDimension(dimension);
        wgContext.setChunkGenerator(chunkGenerator);
        wgContext.setHeightLimitView(heightLimitView);
    }
}