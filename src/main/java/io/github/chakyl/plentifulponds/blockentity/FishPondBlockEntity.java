package io.github.chakyl.plentifulponds.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.network.VanillaPacketDispatcher;
import io.github.chakyl.plentifulponds.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FishPondBlockEntity extends BlockEntity implements TickingBlockEntity {
    protected int progress = 0;
    protected String pondId = "";
    protected ItemStack fishType = ItemStack.EMPTY;
    ;
    protected int population;
    protected int maxPopulation;
    protected int nonNativeFish;
    protected int questId;
    protected boolean questActive;

    public FishPondBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.FISH_POND, pos, state);

    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
    }

    private void sync() {
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        tag.putString("pond_id", this.pondId);
        if (!this.fishType.isEmpty()) {
            tag.putString("fish_type", BuiltInRegistries.ITEM.getKey(this.fishType.getItem()).toString());
        }
        tag.putInt("population", this.population);
        tag.putInt("max_population", this.maxPopulation);
        tag.putInt("non_native_fish", this.nonNativeFish);
        tag.putInt("quest_id", this.questId);
        tag.putBoolean("quest_active", this.questActive);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        this.pondId = tag.getString("pond_id");
        if (tag.contains("fish_type")) {
            ResourceLocation itemId = ResourceLocation.tryParse(tag.getString("fish_type"));
            this.fishType = itemId != null ? BuiltInRegistries.ITEM.get(itemId).getDefaultInstance() : ItemStack.EMPTY;
        } else {
            this.fishType = ItemStack.EMPTY;
        }

        this.population = tag.getInt("population");
        this.maxPopulation = tag.getInt("max_population");
        this.population = tag.getInt("population");
        this.nonNativeFish = tag.getInt("non_native_fish");
        this.questId = tag.getInt("quest_id");
        this.questActive = tag.getBoolean("quest_active");
    }


    public void handleFishExtraction() {
    }

    public void handleFishInsertion(ItemStack stack) {
        if (this.fishType.isEmpty()) {
            this.fishType = stack.copy();
            stack.shrink(1);
        }
    }

    public void handlePondHarvest() {
    }
}