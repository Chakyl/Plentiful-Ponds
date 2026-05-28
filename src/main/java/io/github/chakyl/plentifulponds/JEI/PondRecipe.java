package io.github.chakyl.plentifulponds.JEI;

import io.github.chakyl.plentifulponds.PlentifulPonds;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PondRecipe {
    final ItemStack fish;
    final int maxPopulation;
    final int reproductionRate;
    final ItemStack roe;
    final List<PondDrop> drops;

    public PondRecipe(Pond pond) {
        this.fish = pond.fish().getDefaultInstance();
        this.maxPopulation = pond.maxPopulation();
        this.reproductionRate = pond.reproductionRate();
        this.roe = pond.getRoeItem().copy();
        this.roe.setCount(pond.maxRoe());
        this.drops = pond.pondDrops();
    }

}