package io.github.chakyl.plentifulponds.block;

import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import io.github.chakyl.plentifulponds.PlentifulPonds;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import io.github.chakyl.plentifulponds.screen.FishPondMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class FishPondBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");

    public FishPondBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(UPGRADED, false));
    }


    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, UPGRADED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(UPGRADED, false);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FishPondBlockEntity(pPos, pState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return MenuUtil.openGui(player, pos, FishPondMenu::new);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (hand == InteractionHand.MAIN_HAND && entity instanceof FishPondBlockEntity fishPondBlockEntity) {
            if (player.isCrouching() && stack.isEmpty()) {
                fishPondBlockEntity.handleFishExtraction();
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            } if (!stack.isEmpty()) {
                fishPondBlockEntity.handleFishInsertion(stack);
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            fishPondBlockEntity.handlePondHarvest();
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }
}