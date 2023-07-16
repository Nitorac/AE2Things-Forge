package io.github.projectet.ae2things.compat;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberRootPanel;

import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberXRootPanel;
import io.github.projectet.ae2things.util.XRecipes;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;

import appeng.core.AppEng;
import appeng.recipes.handlers.InscriberRecipe;

import java.util.List;

@JeiPlugin
public class REI implements IModPlugin {
    RecipeType<InscriberRecipe> ID = RecipeType.create(AppEng.MOD_ID, "inscriber", InscriberRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return AE2Things.id("plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new XInscriberRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AE2Things.ADVANCED_INSCRIBER_ITEM.get()), ID);
        registration.addRecipeCatalyst(new ItemStack(AE2Things.ADVANCED_INSCRIBER_X_ITEM.get()), XInscriberRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(XInscriberRecipeCategory.RECIPE_TYPE, List.copyOf(XRecipes.getRecipes(Minecraft.getInstance().level)));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                AdvancedInscriberRootPanel.class,
                82, 39, 26, 16,
                ID);
        registration.addRecipeClickArea(
                AdvancedInscriberXRootPanel.class,
                82, 39, 26, 16,
                ID);
    }
}
