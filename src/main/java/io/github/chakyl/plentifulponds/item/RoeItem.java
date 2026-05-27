package io.github.chakyl.plentifulponds.item;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.ModElements;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;


public class RoeItem extends Item implements ITabFiller {
    public RoeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        DynamicHolder<Pond> pond = getStoredFish(pStack);
        Component roeName;
        if (!pond.isBound()) {
            roeName = Component.translatable("item.plentifulponds.default_roe");
        } else roeName = pond.get().name().plainCopy();
        return Component.translatable(this.getDescriptionId(pStack), roeName);
    }

    public static DynamicHolder<Pond> getStoredFish(ItemStack stack) {
        return stack.getOrDefault(ModElements.Components.FISH, PondRegistry.INSTANCE.emptyHolder());
    }

    public static void setStoredFish(ItemStack stack, Pond fish) {
        setStoredFish(stack, PondRegistry.INSTANCE.holder(fish));
    }

    public static void setStoredFish(ItemStack stack, DynamicHolder<Pond> fish) {
        stack.set(ModElements.Components.FISH, fish);
    }


    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, BuildCreativeModeTabContentsEvent event) {
        PondRegistry.INSTANCE.getKeys().stream().sorted().map(PondRegistry.INSTANCE::holder).forEach(holder -> {
            ItemStack s = new ItemStack(this);
            setStoredFish(s, holder);
            event.accept(s);
        });
    }


}
