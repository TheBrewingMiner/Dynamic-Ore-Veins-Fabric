package net.thebrewingminer.dynamicoreveins.accessor;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public interface IWorldgenContext {
    // Implements storing and retrieving general worldgen context.

    void setChunkGenerator(ChunkGenerator generator);
    ChunkGenerator getChunkGenerator();

    void setHeightLimitView(HeightLimitView heightLimitView);
    HeightLimitView getHeightLimitView();

    void setDimension(RegistryKey<World> dimensionKey);
    RegistryKey<World> getDimension();
}