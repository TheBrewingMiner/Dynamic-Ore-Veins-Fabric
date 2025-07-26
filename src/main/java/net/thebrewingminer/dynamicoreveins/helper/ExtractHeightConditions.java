package net.thebrewingminer.dynamicoreveins.helper;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.thebrewingminer.dynamicoreveins.codec.OreVeinConfig;
import net.thebrewingminer.dynamicoreveins.codec.condition.HeightRangeCondition;
import net.thebrewingminer.dynamicoreveins.codec.condition.IVeinCondition;
import net.thebrewingminer.dynamicoreveins.codec.condition.combination.AllConditions;
import net.thebrewingminer.dynamicoreveins.codec.condition.combination.AnyConditions;
import net.thebrewingminer.dynamicoreveins.codec.condition.combination.NotCondition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtractHeightConditions {
    // Helps "flatten" the condition logic tree of a vein's condition list.
    // In the context of this mod, height ranges of ore veins are not explicitly handled. Instead, they are derived from height range conditions.
    // This helper method essentially processes the config.conditions() object and parses its conditions, ignoring all basic predicates besides
    // height range predicates. This list is then passed onto FindMatchingHeightRange() to determine the valid height range to use for vein
    // generation (edge roundoff).

    private record CacheKey(OreVeinConfig config, RegistryKey<World> dimensionKey){}    // Used to pair which config and dimension of the worldgen context.
    private static final Map<CacheKey, List<HeightRangeCondition>> cachedHeightRangeList = new ConcurrentHashMap<>();   // Used to cache computed lists.


    private ExtractHeightConditions(){}

    public static List<HeightRangeCondition> extractHeightConditions(OreVeinConfig veinConfig, IVeinCondition.Context context) {
        IVeinCondition root = veinConfig.conditions();  // The "conditions" field. Either a predicate or an AllConditions object of predicates.
        List<HeightRangeCondition> flatList = new ArrayList<>();      // The list to return afterward.
        HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.heightLimitViewer()); // Height context to resolve non-absolute height range bounds.
        flattenInto(root, flatList, heightContext);    // Traverse the conditions tree and flatten it into one list.

        return flatList;
    }

    private static void flattenInto(IVeinCondition condition, List<HeightRangeCondition> output, HeightContext heightContext) {
        // Recursively split each combining condition list into a unified output list.
        if (condition instanceof AllConditions all) {
            for (IVeinCondition sub : all.conditions()) {
                flattenInto(sub, output, heightContext);
            }
        } else if (condition instanceof AnyConditions any) {
            for (IVeinCondition sub : any.conditions()) {
                flattenInto(sub, output, heightContext);
            }
        } else if (condition instanceof NotCondition not) {
            // Special case: NOT.
            // Process the inner condition(s) of this object with a temporary list before adding to the output list.
            List<HeightRangeCondition> innerConditions = new ArrayList<>();

            // Recursively collect and flatten all conditions in the NotCondition object before attempting to invert.
            // Store that into the temporary list. This ensures all nested combinations are expanded first.
            for (IVeinCondition sub : not.conditions()) {
                flattenInto(sub, innerConditions, heightContext);
            }

            // Extract all HeightRangeCondition objects from the temporary list.
            List<HeightRangeCondition> innerNotHeightRanges = innerConditions.stream()
                    .filter(c -> c instanceof HeightRangeCondition)                 // Filters for only HeightRangeCondition instances.
                    .toList();

            // Compute the complement of any and all found height ranges in this NOT object.
            computeComplementRanges(innerNotHeightRanges, output, heightContext);
        } else if (condition instanceof HeightRangeCondition heightRange){
            output.add(heightRange);    // Base case: individual height range condition.
                                        // Any other leaf condition is ignored.
        }
    }

    private static void computeComplementRanges(List<HeightRangeCondition> negatedRanges, List<HeightRangeCondition> output, HeightContext heightContext){
        if (negatedRanges.isEmpty()) return;

        List<HeightRangeCondition> sortedRanges = negatedRanges.stream()
                .sorted(Comparator.comparingInt(h -> h.minInclusive().getY(heightContext)))    // Sort ranges in increasing order by minimum y.
                .toList();

        // Level height context.
        int worldMinY = heightContext.getMinY();
        int worldMaxY = worldMinY + heightContext.getHeight();
        int currY = worldMinY;  // Start iterating over ranges up from the minimum y (beginning of the sortedRanges list).

        // Iterate through the height ranges and find gaps.
        for (HeightRangeCondition heightRange : sortedRanges){
            // Info from the current height range.
            int minY = heightRange.minInclusive().getY(heightContext);
            int maxY = heightRange.maxInclusive().getY(heightContext);

            // If there is a gap between the current y and the next minimum y, add the gap as a non-negated height range condition.
            if (currY < minY){
                output.add(new HeightRangeCondition(YOffset.fixed(currY), YOffset.fixed(minY - 1)));
            }
            // Move the current y to the beginning of the next gap (after the next negated height range).
            currY = Math.max(currY, maxY + 1);
        }

        // After going through all height range conditions in the list, check if there is still space between the last height range
        // and the world height limit. If so, add that as a valid height range.
        if (currY <= worldMaxY){
            output.add(new HeightRangeCondition(YOffset.fixed(currY), YOffset.fixed(worldMaxY)));
        }
    }

    public static List<HeightRangeCondition> getOrCacheList(OreVeinConfig veinConfig, IVeinCondition.Context context){
        // Lazily compute the HeightRangeCondition list per config per dimension.
        // Cache it for later calls.
        CacheKey key = new CacheKey(veinConfig, context.dimension());
        return cachedHeightRangeList.computeIfAbsent(key, heightList -> Collections.unmodifiableList(extractHeightConditions(veinConfig, context)));
    }

    public static void clearCache(){
        // Clear the cache.
        cachedHeightRangeList.clear();
    }
}