package io.github.chakyl.plentifulponds.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * Stores all of the information of a pond
 *
 * @param pondId
 * @param displayName
 * @param fish
 * @param cookedVariant
 * @param maxPopulation
 * @param quests
 * @param pondDrops
 */
public record Pond(String pondId, ItemStack fish, ItemStack cookedVariant, Optional<Component> displayName, TextColor color, int maxPopulation, int maxRoe, int reproductionRate, List<PondQuest> quests, List<PondDrop> pondDrops) implements AbstractPond {
    public static final Codec<Pond> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("pond_id").forGetter(Pond::pondId),
                    ItemStack.CODEC.fieldOf("fish").forGetter(Pond::fish),
                    ItemStack.CODEC.fieldOf("cooked_variant").orElse(Items.COAL.getDefaultInstance()).forGetter(Pond::cookedVariant),
                    ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Pond::displayName),
                    TextColor.CODEC.fieldOf("color").forGetter(Pond::color),
                    Codec.intRange(0, 20).fieldOf("max_population").orElse(10).forGetter(Pond::maxPopulation),
                    Codec.intRange(0, Integer.MAX_VALUE / 20).fieldOf("max_roe").orElse(1).forGetter(Pond::maxRoe),
                    Codec.intRange(0, Integer.MAX_VALUE / 20).fieldOf("reproduction_rate").orElse(1).forGetter(Pond::reproductionRate),
                    PondQuest.LIST_CODEC.fieldOf("quests").forGetter(Pond::quests),
                    PondDrop.LIST_CODEC.fieldOf("drops").forGetter(Pond::pondDrops)
            )
            .apply(inst, Pond::new));

    public Pond(Pond other) {
        this(other.pondId, other.fish, other.cookedVariant, other.displayName, other.color, other.maxPopulation, other.maxRoe, other.reproductionRate, other.quests, other.pondDrops);
    }

    @Override
    public Component name() {
        return this.displayName.orElse(this.fish().getHoverName()).copy().withStyle(s -> s.withColor(this.color()));
    }

    public void validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.pondId, "Invalid pondId");
        Preconditions.checkNotNull(this.fish, "Invalid fish!");
    }

    private static List<ItemStack> removeEmptyStacks(List<ItemStack> list) {
        return list.stream().filter(i -> !i.isEmpty()).toList();
    }

    @Override
    public Codec<? extends Pond> getCodec() {
        return CODEC;
    }

    public ItemStack getRoeItem() {
        ItemStack stk = new ItemStack(ModElements.Items.ROE);
        RoeItem.setStoredFish(stk, this);
        return stk;
    }
}