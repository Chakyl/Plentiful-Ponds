package io.github.chakyl.plentifulponds.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.util.DrawsOnLeft;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

public class FishPondScreen extends PlaceboContainerScreen<FishPondMenu> implements DrawsOnLeft {

    public static final int WIDTH = 156;
    public static final int HEIGHT = 168;
    public static final ResourceLocation BASE = loc("textures/gui/fish_pond_gui.png");

    private Pond pond = null;
    private int flavorText = 0;
    private ImageButton btnLeft, btnRight;

    public FishPondScreen(FishPondMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = HEIGHT;
        this.imageWidth = WIDTH;
        RandomSource random = RandomSource.create();
        flavorText = Mth.randomBetweenInclusive(random, 0, 5);
    }

    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTicks) {
        this.pond = this.menu.getPond();


        super.render(gfx, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    public void init() {
        super.init();
//        this.btnLeft = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 13, this.getGuiTop() + 68, 29, 12, LEFT_BUTTON, btn -> {
//            if (this.pond.isBound() && this.currentPage > 0) this.currentPage--;
//        }));
//
//        this.btnRight = this.addRenderableWidget(new ImageButton(this.getGuiLeft() + 46, this.getGuiTop() + 68, 29, 12, RIGHT_BUTTON, btn -> {
//            if (this.pond.isBound() && this.currentPage < this.pond.get().fabDrops().size() / 9) this.currentPage++;
//        }));
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int pX, int pY) {
//        if (this.pond.isBound() && this.selection().mode() == ProductionMode.QUEUE) {
//            gfx.drawString(this.font, Component.translatable("hostilenetworks.gui.queue_current"), QUEUE_X, QUEUE_LABEL_Y, Color.AQUA);
//        }
    }

    @Override
    protected void renderTooltip(GuiGraphics gfx, int pX, int pY) {
        // Fish Stats
        if (this.isHovering(11, 24, 13, 13, pX, pY)) {
            List<Component> txt = new ArrayList<>(4);
            Pond pond = this.menu.getPond();
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.fish_stats", this.menu.getFishType().getDefaultInstance().getHoverName().getString()).withStyle(ChatFormatting.AQUA));
            if (pond.reproductionRate() == 1) {
                txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.reproduction").withStyle(ChatFormatting.GRAY));
            } else {

                txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.reproduction_plural", pond.reproductionRate()).withStyle(ChatFormatting.GRAY));
            }
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.max_population", pond.maxPopulation()).withStyle(ChatFormatting.GRAY));
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.max_roe", pond.maxRoe()).withStyle(ChatFormatting.GRAY));
            gfx.renderComponentTooltip(this.font, txt, pX, pY);
        }
        // Clear Pond
        if (this.isHovering(137, 38, 20, 20, pX, pY)) {
            List<Component> txt = new ArrayList<>(3);
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.clear").withStyle(ChatFormatting.RED));
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.clear_tutorial1").withStyle(ChatFormatting.GRAY));
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.clear_tutorial2").withStyle(ChatFormatting.GRAY));
            gfx.renderComponentTooltip(this.font, txt, pX, pY);
        }
        // Fish Render
        if (this.isHovering(137, 61, 20, 20, pX, pY)) {
            List<Component> txt = new ArrayList<>(2);
            txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.render").withStyle(ChatFormatting.GREEN));
            // TODO: Add enabled/disabled text
            gfx.renderComponentTooltip(this.font, txt, pX, pY);
        }
        // Quest Tooltip
        if (this.menu.isQuestActive() && this.isHovering(60, 142, 80, 21, pX, pY)) {
            ItemStack questItem = this.getQuestItem();
            if (this.isHovering(60 + (80 - 24), 142, 24, 21, pX, pY)) {
                List<Component> txt = new ArrayList<>(getTooltipFromItem(this.minecraft, questItem));
                gfx.renderComponentTooltip(this.font, txt, pX, pY);
            } else {
                List<Component> txt = new ArrayList<>(2);
                txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.bring", questItem.getCount(), questItem.getHoverName()).withStyle(ChatFormatting.GOLD));
                txt.add(Component.translatable("gui.tooltip.plentifulponds.pond.bring_tutorial").withStyle(ChatFormatting.GRAY));
                gfx.renderComponentTooltip(this.font, txt, pX, pY);
            }
        }

        super.renderTooltip(gfx, pX, pY);
    }

    @Override
    public boolean mouseClicked(double pX, double pY, int pButton) {
        return super.mouseClicked(pX, pY, pButton);
    }

    private void click(int id) {
        Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        gfx.blit(BASE, left, top, 0, 0, WIDTH, HEIGHT, 256, 256);

        if (this.pond != null) {
            ItemStack fish = this.menu.getFishType().getDefaultInstance();
            Component titleComponent = Component.translatable("gui.plentifulponds.pond.name", pond.name().getString());
            int boxWidth = 109;

            int centeredX = left + 11 + ((boxWidth - this.font.width(titleComponent)) / 2);

            gfx.drawString(this.font, titleComponent, centeredX, top + 6, 0xFFFFFF, true);
            // Fishies!!!

            int population = this.menu.getPopulation();
            int maxPopulation = this.menu.getMaxPopulation();

            gfx.drawString(this.font, Component.translatable("gui.plentifulponds.pond.population", population, maxPopulation), left + 60, top + 27, 0xFFFFFF, false);

            int fishGridLeft = left + 20;
            int fishGridTop = top + 45;
            boolean smallFishies = maxPopulation > 10;
            int maxFishPerRow = smallFishies ? 10 : 5;
            int slotSize = smallFishies ? 9 : 18;

            float[] originalColor = RenderSystem.getShaderColor().clone();

            int fishInSecondRow = maxPopulation > maxFishPerRow ? maxPopulation - maxFishPerRow : 0;
            if (fishInSecondRow == 0) {
                fishInSecondRow = maxFishPerRow;
                fishGridTop = top + 53;
            }

            for (int i = 0; i < maxPopulation; i++) {
                int row = i / maxFishPerRow;
                int col = i % maxFishPerRow;
                int x = col * slotSize;
                int y = row * slotSize;

                int rowCount = (int) Math.ceil((double) maxPopulation / maxFishPerRow);
                if (row == rowCount - 1) {
                    x += ((maxFishPerRow - fishInSecondRow) * slotSize) / 2;
                }
                if (i == population) {
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                }
                if (smallFishies) {
                    float scale = 0.5F;
                    float offset = (9.0F - (16.0F * scale)) / 2.0F;
                    gfx.pose().pushPose();
                    gfx.pose().translate(fishGridLeft + x + offset, fishGridTop + y + offset, 0.0F);
                    gfx.pose().scale(scale, scale, 1.0F);
                    gfx.renderItem(fish, 0, 0);
                    gfx.pose().popPose();
                } else {
                    gfx.renderItem(fish, fishGridLeft + x, fishGridTop + y);
                }
            }
            RenderSystem.setShaderColor(originalColor[0], originalColor[1], originalColor[2], originalColor[3]);
            // Quests/FlavorText
            if (this.menu.isQuestActive()) {
                gfx.drawWordWrap(this.font, Component.translatable("gui.plentifulponds.pond.flavor_quest" + flavorText), left + 26, top + 100, 92, 0xFFFFFF);
                gfx.blit(BASE, left + 60, top + 142, 0, 176, 80, 21, 256, 256);

                ItemStack questItem = this.getQuestItem();
                gfx.renderItem(questItem, left + 114 + (questItem.getCount() > 9 ? 4 : 0), top + 144);
                gfx.drawString(this.font, Component.translatable("gui.plentifulponds.pond.bring", questItem.getCount()), left + 60 + 15, top + 148, 0xFFFFFF, true);

            } else {
                gfx.drawWordWrap(this.font, Component.translatable("gui.plentifulponds.pond.flavor" + flavorText), left + 26, top + 100, 92, 0xFFFFFF);
            }
        }
    }

    private ItemStack getQuestItem() {
        for (PondQuest quest : pond.quests()) {
            if (quest.population() == this.menu.getMaxPopulation()) {
                return quest.requestedItems().get(this.menu.getQuestId());
            }
        }
        return ItemStack.EMPTY;
    }
}