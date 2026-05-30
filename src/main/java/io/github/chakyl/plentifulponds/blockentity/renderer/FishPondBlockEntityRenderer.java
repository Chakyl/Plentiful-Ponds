package io.github.chakyl.plentifulponds.blockentity.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.block.FishPondBlock;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FishPondBlockEntityRenderer implements BlockEntityRenderer<FishPondBlockEntity> {
    private final ItemRenderer itemRenderer;

    public FishPondBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FishPondBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.hasOutput()) return;
        ItemStack roeStack = ModElements.Items.ROE.value().getDefaultInstance();
        RoeItem.setStoredFish(roeStack, blockEntity.getPond());
        if (roeStack.isEmpty()) return;
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(FishPondBlock.FACING);
        poseStack.pushPose();
        float ticks = blockEntity.getLevel().getGameTime() + partialTick;
        poseStack.translate(0.5, 1.2 + (float) Math.sin(ticks * 0.06f) * 0.05f, 0.5);
        float rotationAngle = facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationAngle));
        poseStack.scale(1.5f, 1.5f, 1.5f);
        this.itemRenderer.renderStatic(
                roeStack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                (int) blockEntity.getBlockPos().asLong()
        );

        poseStack.popPose();
    }
}