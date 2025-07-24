package net.thebrewingminer.dynamicoreveins.accessor;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public interface IDimensionAware {
    // Implement the means to store and retrieve the dimension key.
    void setDimension(RegistryKey<World> dimension);
    RegistryKey<World> getDimension();
}