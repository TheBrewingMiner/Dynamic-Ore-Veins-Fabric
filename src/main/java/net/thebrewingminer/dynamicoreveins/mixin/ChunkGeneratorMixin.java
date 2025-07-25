package net.thebrewingminer.dynamicoreveins.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.thebrewingminer.dynamicoreveins.accessor.IDimensionAware;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin implements IDimensionAware {
    // Cache the owning-dimension of the chunk generator instance.

    @Unique
    private RegistryKey<World> dimension;

    @Override
    public void setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }

    @Override
    public RegistryKey<World> getDimension() {
        return dimension;
    }
}