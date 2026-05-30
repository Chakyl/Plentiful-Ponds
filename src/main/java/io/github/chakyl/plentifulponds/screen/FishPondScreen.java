package io.github.chakyl.plentifulponds.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.util.DrawsOnLeft;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
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

            int fishInSecondRow = maxPopulation % maxFishPerRow;
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
                for (PondQuest quest : pond.quests()) {
                    if (quest.population() == this.menu.getMaxPopulation()) {
                        ItemStack questItem = quest.requestedItems().get(this.menu.getQuestId());
                        gfx.renderItem(questItem, left + 90, top + 119);
                        gfx.drawString(this.font, questItem.getHoverName(), left + 90 + 17, top + 125, 0xFFFFFF, false);
                        break;
                    }
                }
            } else {
                gfx.drawWordWrap(this.font, Component.translatable("gui.plentifulponds.pond.flavor" + flavorText, population, maxPopulation), left + 28, top + 106, 92, 0xFFFFFF);
            }
        }
    }
}