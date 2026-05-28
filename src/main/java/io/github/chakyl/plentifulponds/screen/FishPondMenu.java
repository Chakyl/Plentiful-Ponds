package io.github.chakyl.plentifulponds.screen;


import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import io.github.chakyl.plentifulponds.data.Pond;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FishPondMenu extends BlockEntityMenu<FishPondBlockEntity> {

    // --- Button id scheme (single source of truth, also used by FishPondScreen) ---
    /** Clears the fish type from the pond, effectively resetting it */
    public static final int BTN_CLEAR_POND = -1;

    public FishPondMenu(int id, Inventory pInv, BlockPos pos) {
        super(ModElements.Menus.FISH_POND, id, pInv, pos);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.level().getBlockState(this.pos).is(ModElements.Blocks.FISH_POND);
    }

    /**
     * Routes a clicked button id according to the scheme declared at the top of this class.
     * @see #BTN_CLEAR_POND
     */
    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pId == BTN_CLEAR_POND) {
            this.tile.clearFishType();
            return true;
        }
        return false;
    }

    public Item getFishType() {
        return this.tile.getFishType();
    }

    public int getPopulation() {
        return this.tile.getPopulation();
    }

    public int getMaxPopulation() {
        return this.tile.getMaxPopulation();
    }

    public int getQuestId() {
        return this.tile.getQuestId();
    }

    public boolean isQuestActive() {
        return this.tile.isQuestActive();
    }


    public Pond getPond() {
        return this.tile.getPond();
    }
}