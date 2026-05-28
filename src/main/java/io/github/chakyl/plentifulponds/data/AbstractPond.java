package io.github.chakyl.plentifulponds.data;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public sealed interface AbstractPond extends CodecProvider<Pond> permits Pond {

    Component name();

    TextColor color();

    Item fish();

    Item cookedVariant();

    Fluid pondFluid();

    int maxPopulation();

    int maxRoe();

    int reproductionRate();

    List<PondQuest> quests();

    List<PondDrop> pondDrops();

}