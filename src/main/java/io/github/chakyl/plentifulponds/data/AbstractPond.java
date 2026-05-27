package io.github.chakyl.plentifulponds.data;

import dev.shadowsoffire.placebo.codec.CodecProvider;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public sealed interface AbstractPond extends CodecProvider<Pond> permits Pond {

    String pondId();

    Component name();

    TextColor color();

    ItemStack fish();

    ItemStack cookedVariant();

    int maxPopulation();

    int maxRoe();

    int reproductionRate();

    List<PondQuest> quests();

    List<PondDrop> pondDrops();

}