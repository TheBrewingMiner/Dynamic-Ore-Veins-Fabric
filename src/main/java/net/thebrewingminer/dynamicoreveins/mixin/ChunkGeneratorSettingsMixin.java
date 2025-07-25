package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.thebrewingminer.dynamicoreveins.accessor.IWorldgenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkGeneratorSettings.class)
public class ChunkGeneratorSettingsMixin implements IWorldgenContext {

    @Unique private ChunkGenerator chunkGenerator;
    @Unique private HeightLimitView heightViewer;
    @Unique private RegistryKey<World> dimension;

    @Override
    public void setChunkGenerator(ChunkGenerator generator) {
        this.chunkGenerator = generator;
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    @Override
    public void setHeightLimitView(HeightLimitView heightLimitView) {
        this.heightViewer = heightLimitView;
    }

    @Override
    public HeightLimitView getHeightLimitView() {
        return this.heightViewer;
    }

    @Override
    public void setDimension(RegistryKey<World> dimensionKey) {
        this.dimension = dimensionKey;
    }

    @Override
    public RegistryKey<World> getDimension() {
        return this.dimension;
    }
}