package net.thebrewingminer.dynamicoreveins.codec.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class IsDimension {
    // Handle a dimension resource key or list of dimension resource keys.
    public static final Codec<List<RegistryKey<World>>> CODEC = Codec.either(
        RegistryKey.createCodec(RegistryKeys.WORLD),
        RegistryKey.createCodec(RegistryKeys.WORLD).listOf()
    ).xmap(
        either -> either.map(
                Collections::singletonList,
                (List<RegistryKey<World>> list) -> list
        ),
        list -> {
            if (list.size() == 1){
                return Either.left(list.get(0));
            } else {
                return Either.right(list);
            }
        }
    );
}