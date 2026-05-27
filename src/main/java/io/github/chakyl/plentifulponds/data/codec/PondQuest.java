package io.github.chakyl.plentifulponds.data.codec;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.json.OptionalStackCodec;
import io.github.chakyl.plentifulponds.data.Pond;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public record PondQuest(int population, List<ItemStack> requestedItems) {
    public static final Codec<PondQuest> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.intRange(0, Integer.MAX_VALUE / 20).fieldOf("population").forGetter(PondQuest::population),
                    OptionalStackCodec.INSTANCE.listOf().xmap(PondQuest::removeEmptyStacks, Function.identity()).fieldOf("requested_items").forGetter(PondQuest::requestedItems)
            )
            .apply(inst, PondQuest::new));

    public PondQuest(PondQuest other) {
        this(other.population, other.requestedItems);
    }
    public static final Codec<List<PondQuest>> LIST_CODEC = CODEC.listOf();

    private static List<ItemStack> removeEmptyStacks(List<ItemStack> list) {
        return list.stream().filter(i -> !i.isEmpty()).toList();
    }

}