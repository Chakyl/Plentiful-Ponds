package io.github.chakyl.plentifulponds.JEI;

import io.github.chakyl.plentifulponds.PlentifulPonds;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.ModElements;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FishPondCategory implements IRecipeCategory<PondRecipe> {

    public static final RecipeType<PondRecipe> TYPE = RecipeType.create(PlentifulPonds.MODID, "fish_farming", PondRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component name;

    public FishPondCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(177, 61);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModElements.Items.SEA_BISCUIT.value()));
        this.name = Component.translatable(ModElements.Items.SEA_BISCUIT.value().getDescriptionId());
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }


    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public Component getTitle() {
        return this.name;
    }

    @Override
    public RecipeType<PondRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PondRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).addItemStack(recipe.fish).setStandardSlotBackground();;
        int slotSize = 21;
        builder.addSlot(RecipeIngredientRole.OUTPUT, 26, 2).addItemStack(recipe.roe).setStandardSlotBackground();;
        for (int i = 1; i < recipe.drops.size() + 1; i++) {
            int line = i > 6 ? 28 : 2;
            PondDrop drop = recipe.drops.get(i - 1);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 26 + (i > 6 ? i - 7 : i) * slotSize, line).addItemStack(drop.drop()).addRichTooltipCallback((view, tooltip) -> {
                tooltip.add(Component.translatable("jei.plentifulponds.fish_farming.population", drop.minPopulation()).withStyle(ChatFormatting.AQUA));
                if (drop.chance() < 1) {
                    tooltip.add(Component.translatable("jei.plentifulponds.fish_farming.chance", Math.round(drop.chance() * 100)).withStyle(ChatFormatting.GOLD));
                }
            }).setStandardSlotBackground();
        }
    }

    @Override
    public void draw(PondRecipe pondRecipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(pondRecipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.plentifulponds.fish_farming.info"), 2, 46, 4210752, false);

    }

}