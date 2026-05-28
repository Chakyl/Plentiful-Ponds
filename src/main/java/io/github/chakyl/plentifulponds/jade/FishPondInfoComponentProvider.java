package io.github.chakyl.plentifulponds.jade;

import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;


public enum FishPondInfoComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        boolean isEmpty = true;
        if (accessor.getServerData().contains("fishType")) {
            IElementHelper elements = IElementHelper.get();
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(accessor.getServerData().getString("fishType")));
            if (item != Items.AIR) {
                isEmpty = false;
                int population = accessor.getServerData().getInt("population");
                int maxPopulation = accessor.getServerData().getInt("maxPopulation");
                tooltip.add(Component.translatable("jade.plentifulponds.fish_pond.header", item.getDescription(), population, maxPopulation));
                tooltip.add(elements.spacer(0, 0));
                for (int i = 0; i < maxPopulation; i++) {
                    if (i >= population) {
                        // TODO: Make item black or have placeholder image
                        item = Items.COAL;
                    }
                    tooltip.append(elements.item(item.getDefaultInstance(), 0.5f).size(new Vec2(6, 10)).translate(new Vec2(i, -1)));
                }
                tooltip.add(elements.spacer(14, 2));
            }
        }
        if (isEmpty){
            tooltip.add(Component.translatable("jade.plentifulponds.fish_pond.no_fish"));
            tooltip.add(Component.translatable("jade.plentifulponds.fish_pond.insert"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        FishPondBlockEntity pond = (FishPondBlockEntity) accessor.getBlockEntity();
        data.putString("fishType", BuiltInRegistries.ITEM.getKey(pond.getFishType()).toString());
        data.putInt("population", pond.getPopulation());
        data.putInt("maxPopulation", pond.getMaxPopulation());
    }

    @Override
    public ResourceLocation getUid() {
        return io.github.chakyl.plentifulponds.jade.PondInfoPlugin.UID;
    }

}