package net.thebrewingminer.dynamicoreveins.main;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thebrewingminer.dynamicoreveins.codec.OreRichnessSettings;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;
import net.thebrewingminer.dynamicoreveins.helper.HeightRangeWrapper;

import java.util.List;

import static net.thebrewingminer.dynamicoreveins.helper.FindMatchingHeightRange.findMatchingHeightRange;
import static net.thebrewingminer.dynamicoreveins.helper.InThresholdHelper.inThreshold;
import static net.thebrewingminer.dynamicoreveins.registry.OreVeinRegistryHolder.getActiveDebugSettings;

public class DynamicOreVeinSampler {
    private DynamicOreVeinSampler(){}

    public static BlockState selectVein(DensityFunction.NoisePos functionContext, DensityFunction routerVeinToggle, DensityFunction routerVeinRidged, DensityFunction routerVeinGap, List<OreVeinConfig> veinList, IVeinCondition.Context veinContext){
        DensityFunction veinToggle = routerVeinToggle;
        DensityFunction veinRidged = routerVeinRidged;
        DensityFunction veinGap = routerVeinGap;

        OreVeinConfig selectedConfig = null;

        for (OreVeinConfig veinConfig : veinList) {
            DensityFunction localVeinToggle;
            DensityFunction localVeinRidged;
            DensityFunction localVeinGap;

            /* Check if in suitable dimension. */
            if (!veinConfig.dimension().contains(veinContext.dimension())) continue;

            /* Use configured vein toggle and shaping density functions, if specified */
            localVeinToggle = (veinConfig.veinToggle().rawFunction() != null ? veinConfig.veinToggle().getOrMapFunction(veinContext) : routerVeinToggle);
            localVeinRidged = (veinConfig.veinRidged().rawFunction() != null ? veinConfig.veinRidged().getOrMapFunction(veinContext) : routerVeinRidged);
            localVeinGap = (veinConfig.veinGap().rawFunction() != null ? veinConfig.veinGap().getOrMapFunction(veinContext) : routerVeinGap);

            /* Calculate if in toggle's threshold */
            if (!inThreshold(localVeinToggle, veinConfig.veinToggle().minThreshold(), veinConfig.veinToggle().maxThreshold(), veinContext)) continue;

            /* Test the vein's conditions. If passed, set the relevant info and call the next function */
            if (veinConfig.conditions().test(veinContext)){
                selectedConfig = veinConfig;
                veinToggle = localVeinToggle;
                veinRidged = localVeinRidged;
                veinGap = localVeinGap;
                break;
            }
        }

        if (selectedConfig == null) return null;                                                // If no config was found, return null.

        HeightRangeWrapper heightRange = findMatchingHeightRange(selectedConfig, veinContext);  // Build a height range for the selected vein.

        if (getActiveDebugSettings().printHeightRange()) System.out.println(heightRange);  // Debug.

        return dynamicOreVeinSampler(functionContext, veinToggle, veinRidged, veinGap, selectedConfig, veinContext, heightRange);
    }

    public static BlockState dynamicOreVeinSampler(DensityFunction.NoisePos functionContext, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, OreVeinConfig config, IVeinCondition.Context veinContext, HeightRangeWrapper heightRange){
        Random seededRandom = veinContext.randomSplitter().split(veinContext.pos());

        // Get all relevant info from the config.
        BlockState ore = config.ore().get(seededRandom, veinContext.pos());
        BlockState secondaryOre = config.secondaryOre().get(seededRandom, veinContext.pos());
        BlockState fillerBlock = config.fillerBlock().get(seededRandom, veinContext.pos());
        float secondaryOreChance = config.secondaryOreChance();
        OreRichnessSettings settings = config.veinSettings();

        BlockState empty = null;    // Default value.

        // Vanilla veinifier algorithm, de-hardcoded.
        double toggleValue = veinToggle.sample(functionContext);
        int currY = veinContext.pos().getY();
        double toggleStrength = Math.abs(toggleValue);
        int relativeToMinY = (currY - heightRange.min_y());
        int relativeToMaxY = (heightRange.max_y() - currY);
        if (relativeToMinY >= 0 && relativeToMaxY >= 0){
            int relativeToVeinBoundary = Math.min(relativeToMinY, relativeToMaxY);
            double boundClamped = MathHelper.clampedMap(relativeToVeinBoundary, 0.0, settings.edgeRoundOffBegin(), -settings.maxEdgeRoundOff(), 0.0);
            if((toggleStrength + boundClamped) < settings.minRichnessThreshold()){
                return empty;
            } else {
                if(seededRandom.nextFloat() > settings.veinSolidness()){
                    return empty;
                } else if (veinRidged.sample(functionContext) >= 0.0){
                    return empty;
                } else {
                    double richness = MathHelper.clampedMap(toggleStrength, settings.minRichnessThreshold(), settings.maxRichnessThreshold(), settings.minRichness(), settings.maxRichness());
                    if(((double)seededRandom.nextFloat() < richness) && (veinGap.sample(functionContext) > settings.skipOreThreshold())){
                        if (getActiveDebugSettings().printSuccessPos()) System.out.println("Success! " + veinContext.pos().toString());       // Debug
                        return (seededRandom.nextFloat() < secondaryOreChance ? secondaryOre : ore);
                    } else {
                        if (getActiveDebugSettings().printSuccessPos()) System.out.println("Success! " + veinContext.pos().toString());      // Debug
                        return fillerBlock;
                    }
                }
            }
        } else return empty;
    }
}