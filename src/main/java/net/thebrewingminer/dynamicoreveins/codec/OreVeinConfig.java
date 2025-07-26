package net.thebrewingminer.dynamicoreveins.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.thebrewingminer.dynamicoreveins.codec.condition.DensityFunctionThreshold;
import net.thebrewingminer.dynamicoreveins.codec.condition.HeightRangeCondition;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;
import net.thebrewingminer.dynamicoreveins.codec.condition.IsDimension;
import net.thebrewingminer.dynamicoreveins.registry.VeinConditionRegistry;

import java.util.Collections;
import java.util.List;

public record OreVeinConfig(BlockStateProvider ore, BlockStateProvider secondaryOre, float secondaryOreChance, BlockStateProvider fillerBlock, DensityFunctionThreshold veinToggle, DensityFunctionThreshold veinRidged, DensityFunctionThreshold veinGap, OreRichnessSettings veinSettings, List<RegistryKey<World>> dimension, IVeinCondition conditions){
    // The main config for worldgen devs to implement custom large ore veins.
    public static final Codec<OreVeinConfig> CODEC = RecordCodecBuilder.create(oreVeinConfigInstance -> oreVeinConfigInstance.group(
            ResourceKeyOrBlockState.CODEC.fieldOf("ore").forGetter(OreVeinConfig::ore),
            ResourceKeyOrBlockState.CODEC.fieldOf("secondary_ore").forGetter(OreVeinConfig::secondaryOre),
            Codec.floatRange(0.0f, 1.0f).fieldOf("secondary_ore_chance").orElse(0.02f).forGetter(OreVeinConfig::secondaryOreChance),
            ResourceKeyOrBlockState.CODEC.fieldOf("filler_block").forGetter(OreVeinConfig::fillerBlock),
            DensityFunctionThreshold.CODEC.optionalFieldOf("vein_toggle", DensityFunctionThreshold.createDefault()).forGetter(OreVeinConfig::veinToggle),
            DensityFunctionThreshold.CODEC.optionalFieldOf("vein_ridged", DensityFunctionThreshold.createDefault()).forGetter(OreVeinConfig::veinRidged),   // These don't necessarily need to be DensityFunctionThreshold
            DensityFunctionThreshold.CODEC.optionalFieldOf("vein_gap", DensityFunctionThreshold.createDefault()).forGetter(OreVeinConfig::veinGap),         // objects. It was just easier for nullability.
            OreRichnessSettings.CODEC.optionalFieldOf("vein_settings", OreRichnessSettings.createDefault()).forGetter(OreVeinConfig::veinSettings),
            IsDimension.CODEC.fieldOf("dimension").orElse(Collections.singletonList(World.OVERWORLD)).forGetter(OreVeinConfig::dimension),
            VeinConditionRegistry.codec().optionalFieldOf("conditions", HeightRangeCondition.createDefault()).forGetter(OreVeinConfig::conditions)
    ).apply(oreVeinConfigInstance, OreVeinConfig::new));
}