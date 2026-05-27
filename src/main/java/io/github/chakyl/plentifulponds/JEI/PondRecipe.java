package io.github.chakyl.plentifulponds.JEI;

import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PondRecipe {
    final ItemStack fish;
    final ItemStack roe;
    final List<PondDrop> drops;

    public PondRecipe(Pond pond) {
        this.fish = pond.fish().copy();
        this.roe = pond.getRoeItem().copy();
        this.drops = pond.pondDrops();
    }

}