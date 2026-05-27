package io.github.chakyl.plentifulponds.data.codec;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PondDrop(int minPopulation, ItemStack drop, float chance) {
    public static final Codec<PondDrop> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.intRange(0, Integer.MAX_VALUE / 20).fieldOf("min_population").forGetter(PondDrop::minPopulation),
                    ItemStack.CODEC.fieldOf("drop").forGetter(PondDrop::drop),
                    Codec.FLOAT.optionalFieldOf("chance", -1F).forGetter(PondDrop::chance))
            .apply(inst, PondDrop::new));

    public static final Codec<List<PondDrop>> LIST_CODEC = CODEC.listOf();

    public PondDrop(PondDrop other) {
        this(other.minPopulation, other.drop, other.chance);
    }

}