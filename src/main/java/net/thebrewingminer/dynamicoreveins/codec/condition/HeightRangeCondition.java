package net.thebrewingminer.dynamicoreveins.codec.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public record HeightRangeCondition(YOffset minInclusive, YOffset maxInclusive) implements IVeinCondition {
    // Encodes vertical anchors into a height range.
    public static final Codec<HeightRangeCondition> CODEC = RecordCodecBuilder.create(heightRangeConditionInstance -> heightRangeConditionInstance.group(
            YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(HeightRangeCondition::minInclusive),
            YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(HeightRangeCondition::maxInclusive)
    ).apply(heightRangeConditionInstance, HeightRangeCondition::new));

    @Override
    public String type(){
        // String identifier.
        return "dynamic_veins:height_range";
    }

    @Override
    public boolean test(IVeinCondition.Context context){
        // Test if the context's position is within the height range.
        HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.heightLimitViewer());
        int minY = minInclusive.getY(heightContext);
        int maxY = maxInclusive.getY(heightContext);
        int posY = context.pos().getY();
        return (posY >= minY && posY <= maxY);
    }

    public static HeightRangeCondition createDefault(){
        // Default to height range [bottomY + 4, topY - 4].
        return new HeightRangeCondition(YOffset.aboveBottom(4), YOffset.belowTop(4));
    }
}