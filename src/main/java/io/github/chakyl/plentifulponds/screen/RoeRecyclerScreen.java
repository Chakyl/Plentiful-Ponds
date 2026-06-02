package io.github.chakyl.plentifulponds.screen;

import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.util.DrawsOnLeft;
import io.github.chakyl.plentifulponds.blockentity.RoeRecyclerBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

public class RoeRecyclerScreen extends PlaceboContainerScreen<RoeRecyclerMenu> implements DrawsOnLeft {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final int PROGRESS_TEXTURE_X = 0;
    private static final int PROGRESS_TEXTURE_Y = 166;

    private static final int PROGRESS_HEIGHT = 12;
    private static final int PROGRESS_GUI_X = 51;
    private static final int PROGRESS_GUI_Y = 36;


    public static final ResourceLocation BASE = loc("textures/gui/roe_recycler.png");
    public RoeRecyclerScreen(RoeRecyclerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = HEIGHT;
        this.imageWidth = WIDTH;
    }

    @Override
    public void render(GuiGraphics gfx, int pMouseX, int pMouseY, float pPartialTicks) {

        super.render(gfx, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void renderTooltip(GuiGraphics gfx, int pX, int pY) {
        if (this.isHovering(6, 10, 7, 53, pX, pY)) {
            super.renderTooltip(gfx, pX, pY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float pPartialTicks, int pX, int pY) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        gfx.blit(BASE, left, top , 0, 0, WIDTH, HEIGHT, 256, 256);
        // Progress Bar
        int progress = this.menu.getProgress();
        int maxProgress = 200;
        int progressArrowSize = 35;

        int currentWidth =  progress != 0 ? Mth.clamp(progress * progressArrowSize / maxProgress, 1, progressArrowSize) : 0;
        gfx.blit(BASE, left + PROGRESS_GUI_X, top + PROGRESS_GUI_Y, PROGRESS_TEXTURE_X, PROGRESS_TEXTURE_Y, currentWidth, PROGRESS_HEIGHT);


    }
}
