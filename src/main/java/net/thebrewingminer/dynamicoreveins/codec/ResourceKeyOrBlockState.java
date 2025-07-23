package net.thebrewingminer.dynamicoreveins.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class ResourceKeyOrBlockState {
    public static final Codec<BlockStateProvider> CODEC = Codec.either(
        Identifier.CODEC,
        BlockStateProvider.TYPE_CODEC
    ).xmap(
        either -> either.map(
            location -> BlockStateProvider.of(Registries.BLOCK.getOrThrow(RegistryKey.of(RegistryKeys.BLOCK, location)).getDefaultState()),
            provider -> provider
        ),
        stateProvider -> {
            if (stateProvider instanceof SimpleBlockStateProvider simpleProvider){
                Block block = simpleProvider.get(Random.createLocal(), new BlockPos(BlockPos.ZERO)).getBlock();
                Identifier location = Registries.BLOCK.getId(block);
                return Either.left(location);
            } else {
                return Either.right(stateProvider);
            }
        }
    );
}