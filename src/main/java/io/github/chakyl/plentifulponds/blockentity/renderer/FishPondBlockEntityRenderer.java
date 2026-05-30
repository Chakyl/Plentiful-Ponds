package io.github.chakyl.plentifulponds.blockentity.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.block.FishPondBlock;
import io.github.chakyl.plentifulponds.blockentity.FishPondBlockEntity;
import io.github.chakyl.plentifulponds.item.RoeItem;
import io.github.chakyl.plentifulponds.model.ExclamationModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

public class FishPondBlockEntityRenderer implements BlockEntityRenderer<FishPondBlockEntity> {
    private final ItemRenderer itemRenderer;
    private static final ResourceLocation QUEST_TEXTURE = loc("textures/block/pond_quest.png");
    private static final ResourceLocation ERROR_TEXTURE = loc("textures/block/pond_error.png");
    private final ExclamationModel model;

    public FishPondBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.model = new ExclamationModel(context.bakeLayer(ExclamationModel.LAYER_LOCATION));
    }

    private void renderExclamation(ResourceLocation texture, FishPondBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180F));
        float ticks = (blockEntity.getLevel().getGameTime() + partialTick);
        poseStack.translate(0, -1.2 + (float) Math.sin(ticks * 0.06f) * 0.05f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(ticks * 2F));
        var vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void render(FishPondBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.isValid()) {
            renderExclamation(ERROR_TEXTURE, blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        } else if (blockEntity.isQuestActive()) {
            renderExclamation(QUEST_TEXTURE, blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        } else if (blockEntity.hasOutput()) {
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
        if (blockEntity.getFishType() != null) {
            ItemStack fish = blockEntity.getFishType().getDefaultInstance();
            if (fish.isEmpty()) return;
            BlockState state = blockEntity.getBlockState();
            Direction facing = state.getValue(FishPondBlock.FACING);
            poseStack.pushPose();
            poseStack.translate(0.5 + (facing.getStepX() * 0.5), 0.2, 0.5 + (facing.getStepZ() * 0.5));
            float rotationAngle = facing.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-rotationAngle));

            poseStack.scale(0.75f, 0.75f, 0.75f);
            this.itemRenderer.renderStatic(
                    fish,
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
}