package io.github.chakyl.plentifulponds.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class RoeRecyclerBlockEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister {
    protected final RoeRecyclerItemHandler inventory = new RoeRecyclerItemHandler();
    protected final int PROGRESS_TIME = 200;

    protected int progress;
    protected final SimpleDataSlots data = new SimpleDataSlots();

    public RoeRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.ROE_RECYCLER, pos, state);
        this.data.addData(() -> this.progress, v -> this.progress = v);

    }


    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level.hasNeighborSignal(pos)) return;
        if (!this.inventory.getStackInSlot(0).isEmpty() && this.inventory.getStackInSlot(0).is(ModElements.Items.ROE.value())) {
            if (this.progress >= PROGRESS_TIME) {
                ItemStack roeItem = this.inventory.getStackInSlot(0);
                ItemStack roeResult = getRoeRecycle(roeItem);
                boolean inserted = false;
                if (!roeResult.isEmpty()) {
                    for (int i = 1; i < 9; i++) {
                        if (this.inventory.getStackInSlot(i).isEmpty()) {
                            this.inventory.insertItemInternal(i, roeResult, false);
                            inserted = true;
                            break;
                        } else if (this.inventory.getStackInSlot(i).is(roeResult.getItem()) && this.inventory.getStackInSlot(i).getCount() + roeResult.getCount() < roeResult.getMaxStackSize()) {
                            this.inventory.insertItemInternal(i, roeResult, false);
                            inserted = true;
                            break;
                        }
                    }
                    if (inserted) {
                        this.progress = 0;
                        this.setChanged();
                        roeItem.shrink(1);
                        ((ServerLevel) level).sendParticles(ParticleTypes.BUBBLE, pos.getX(), pos.getY(), pos.getZ(), 4, 0.5, 2, 0.5, 0.002);
                    }
                }
            } else {
                this.progress++;
            }
        }
    }

    public ItemStack getRoeRecycle(ItemStack roeItem) {
        DynamicHolder<Pond> pondHold = RoeItem.getStoredFish(roeItem);
        if (!pondHold.isBound()) return ItemStack.EMPTY;
        Pond pond = pondHold.get();
        int resolvedPopulation = Mth.randomBetweenInclusive(this.level.getRandom(), 1, pond.maxPopulation());
        if (Math.random() < 0.75 && resolvedPopulation > pond.maxPopulation() / 2) {
            resolvedPopulation = Mth.randomBetweenInclusive(this.level.getRandom(), 1, pond.maxPopulation() / 2);
        }
        resolvedPopulation = Mth.floor(resolvedPopulation);
        if (pond.pondDrops() != null && !pond.pondDrops().isEmpty()) {
            for (PondDrop drop : pond.pondDrops()) {
                double roll = Math.random();
                if (resolvedPopulation >= drop.minPopulation() && roll <= drop.chance() / 2) {
                    int rewardCount = (int) Math.floor((double) (Math.max(1, drop.drop().getCount() / 4) * ((resolvedPopulation - drop.minPopulation()))) / (10 - drop.minPopulation()));
                    if (resolvedPopulation == pond.maxPopulation()) rewardCount = drop.drop().getCount();
                    ItemStack reward = drop.drop().copy();
                    reward.setCount(rewardCount);
                    return reward.copy();
                }
            }
        }
        return ModElements.Items.POND_SCUM.value().getDefaultInstance().copy();
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        tag.put("inventory", this.inventory.serializeNBT(regs));
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        this.inventory.deserializeNBT(regs, tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    public RoeRecyclerItemHandler getInventory() {
        return this.inventory;
    }

    public int getProgress() {
        return this.progress;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

    public class RoeRecyclerItemHandler extends InternalItemHandler {

        public RoeRecyclerItemHandler() {
            super(10);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.is(ModElements.Items.ROE);
            return true;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot > 0) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            RoeRecyclerBlockEntity.this.setChanged();
        }

        public NonNullList<ItemStack> getItems() {
            return this.stacks;
        }
    }
}