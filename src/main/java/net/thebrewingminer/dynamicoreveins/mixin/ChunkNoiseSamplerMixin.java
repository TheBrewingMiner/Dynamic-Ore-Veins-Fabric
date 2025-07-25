package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.thebrewingminer.dynamicoreveins.accessor.ISettingsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkNoiseSampler.class)
public class ChunkNoiseSamplerMixin implements ISettingsAccessor {

    @Unique ChunkGeneratorSettings cachedChunkGenSettings;
    @Unique NoiseConfig cachedNoiseConfig;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstruct(int horizontalCellCount, NoiseConfig noiseConfig, int startBlockX, int startBlockZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender, CallbackInfo ci){
        this.cachedChunkGenSettings = chunkGeneratorSettings;      // Cache the chunk generator settings and noise config passed into the constructor
        this.cachedNoiseConfig = noiseConfig;                      // into the owning ChunkNoiseSampler object.
    }

    @Override
    public ChunkGeneratorSettings getChunkGenSettings() {
        return cachedChunkGenSettings;
    }

    @Override
    public NoiseConfig getNoiseConfig() {
        return cachedNoiseConfig;
    }
}