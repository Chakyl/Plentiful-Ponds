package io.github.chakyl.splendidslimes.JEI;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.plentifulponds.JEI.FishPondCategory;
import io.github.chakyl.plentifulponds.JEI.PondRecipe;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.item.AgedRoeItem;
import io.github.chakyl.plentifulponds.item.RoeItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.plentifulponds.PlentifulPonds.loc;

@JeiPlugin
public class PlentifulPondsJei implements IModPlugin {

    public static final ResourceLocation UID = loc("plugin");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModElements.Items.ROE.value(), new RoeSubtypes());
        reg.registerSubtypeInterpreter(ModElements.Items.AGED_ROE.value(), new AgedRoeSubtypes());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        reg.addRecipeCategories(new FishPondCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PondRecipe> pondRecipes = new ArrayList<>();
        for (Pond pond : PondRegistry.INSTANCE.getValues()) {
            pondRecipes.add(new PondRecipe(pond));
        }
        registration.addRecipes(FishPondCategory.TYPE, pondRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(ModElements.Blocks.FISH_POND.value()), FishPondCategory.TYPE);
    }

    private static class RoeSubtypes implements ISubtypeInterpreter<ItemStack> {

        @Override
        public DynamicHolder<Pond> getSubtypeData(ItemStack stack, UidContext context) {
            return RoeItem.getStoredFish(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<Pond> fish = RoeItem.getStoredFish(stack);
            if (!fish.isBound()) return "NULL";
            return fish.getId().toString();
        }

    }

    private static class AgedRoeSubtypes implements ISubtypeInterpreter<ItemStack> {

        @Override
        public DynamicHolder<Pond> getSubtypeData(ItemStack stack, UidContext context) {
            return AgedRoeItem.getStoredFish(stack);
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            DynamicHolder<Pond> fish = AgedRoeItem.getStoredFish(stack);
            if (!fish.isBound()) return "NULL";
            return fish.getId().toString();
        }

    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

}