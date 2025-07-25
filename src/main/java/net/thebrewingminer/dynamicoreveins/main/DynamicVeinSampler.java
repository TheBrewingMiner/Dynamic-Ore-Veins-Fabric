package net.thebrewingminer.dynamicoreveins.main;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;

import java.util.List;

public class DynamicVeinSampler {
    private DynamicVeinSampler(){}

    public static BlockState selectVein(DensityFunction.NoisePos functionContext, DensityFunction routerVeinToggle, DensityFunction routerVeinRidged, DensityFunction routerVeinGap, List<OreVeinConfig> veinList, IVeinCondition.Context veinContext){
        return null;
    }
}