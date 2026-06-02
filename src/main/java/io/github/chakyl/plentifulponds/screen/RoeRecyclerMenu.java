package io.github.chakyl.plentifulponds.screen;


import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.blockentity.RoeRecyclerBlockEntity;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class RoeRecyclerMenu extends BlockEntityMenu<RoeRecyclerBlockEntity> {

    public RoeRecyclerMenu(int id, Inventory pInv, BlockPos pos) {
        super(ModElements.Menus.ROE_RECYCLER, id, pInv, pos);
        RoeRecyclerBlockEntity.RoeRecyclerItemHandler inv = this.tile.getInventory();
        this.addSlot(new FilteredSlot(inv, 0, 26, 34, s -> s.is(ModElements.Items.ROE)));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new FilteredSlot(inv, 1 + y * 3 + x, 98 + x * 18, 15 + y * 18, s -> false));
            }
        }
        this.addPlayerSlots(pInv, 8, 84);
        this.mover.registerRule((stack, slot) -> slot == 0, 9, this.slots.size());
        this.mover.registerRule((stack, slot) -> stack.getItem() instanceof RoeItem, 0, 1);
        this.mover.registerRule((stack, slot) -> slot < 9, 9, this.slots.size());
        this.registerInvShuffleRules();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(ModElements.Blocks.ROE_RECYCLER);

    }
    public int getProgress() {
        return this.tile.getProgress();
    }

}