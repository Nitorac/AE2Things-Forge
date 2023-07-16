package io.github.projectet.ae2things.util;

import javax.annotation.Nullable;

import appeng.blockentity.misc.InscriberRecipes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.materials.NamePressItem;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class indexes all inscriber recipes to find valid inputs for the top and bottom optional slots. This speeds up
 * checks whether inputs for those two slots are valid.
 */
public final class XRecipes {

    public static final ResourceLocation NAMEPLATE_RECIPE_ID = new ResourceLocation(AppEng.MOD_ID, "nameplate");

    private static ArrayList<InscriberRecipe> cached_recipes;

    private XRecipes() {
    }

    public static boolean isPrint(ItemStack in){
        return AEItems.SILICON_PRINT.isSameAs(in) ||
                AEItems.CALCULATION_PROCESSOR_PRINT.isSameAs(in) ||
                AEItems.ENGINEERING_PROCESSOR_PRINT.isSameAs(in) ||
                AEItems.LOGIC_PROCESSOR_PRINT.isSameAs(in);
    }

    /**
     * Returns an unmodifiable view of all registered inscriber recipes.
     */
    public static Collection<InscriberRecipe> getRecipes(Level level) {
        if(cached_recipes == null){
            cached_recipes = new ArrayList<>();
            for(InscriberRecipe recipe : InscriberRecipes.getRecipes(level)){

                cached_recipes.add(new InscriberRecipe(
                        recipe.getId(),
                        recipe.getMiddleInput(),
                        recipe.getResultItem(),
                        isPrint(recipe.getResultItem()) ? Ingredient.EMPTY : recipe.getTopOptional(),
                        recipe.getBottomOptional(),
                        recipe.getProcessType()
                ));
            }
        }
        return cached_recipes;
    }

    @Nullable
    public static InscriberRecipe findRecipe(Level level, ItemStack input, ItemStack plateA, ItemStack plateB,
                                             boolean supportNamePress) {
        if (supportNamePress) {
            boolean isNameA = AEItems.NAME_PRESS.isSameAs(plateA);
            boolean isNameB = AEItems.NAME_PRESS.isSameAs(plateB);

            if (isNameA && isNameB || isNameA && plateB.isEmpty()) {
                return makeNamePressRecipe(input, plateA, plateB);
            } else if (plateA.isEmpty() && isNameB) {
                return makeNamePressRecipe(input, plateB, plateA);
            }
        }

        for (InscriberRecipe recipe : getRecipes(level)) {
            // The recipe can be flipped at will
            final boolean matchA = recipe.getTopOptional().test(plateA) && recipe.getBottomOptional().test(plateB);
            final boolean matchB = recipe.getTopOptional().test(plateB) && recipe.getBottomOptional().test(plateA);

            if ((matchA || matchB) && recipe.getMiddleInput().test(input)) {
                return recipe;
            }
        }

        return null;
    }

    private static InscriberRecipe makeNamePressRecipe(ItemStack input, ItemStack plateA, ItemStack plateB) {
        String name = "";

        if (!plateA.isEmpty()) {
            final CompoundTag tag = plateA.getOrCreateTag();
            name += tag.getString(NamePressItem.TAG_INSCRIBE_NAME);
        }

        if (!plateB.isEmpty()) {
            final CompoundTag tag = plateB.getOrCreateTag();
            name += " " + tag.getString(NamePressItem.TAG_INSCRIBE_NAME);
        }

        final Ingredient startingItem = Ingredient.of(input.copy());
        final ItemStack renamedItem = input.copy();

        if (!name.isEmpty()) {
            renamedItem.setHoverName(Component.literal(name));
        } else {
            renamedItem.setHoverName(null);
        }

        final InscriberProcessType type = InscriberProcessType.INSCRIBE;

        return new InscriberRecipe(NAMEPLATE_RECIPE_ID, startingItem, renamedItem,
                plateA.isEmpty() ? Ingredient.EMPTY : Ingredient.of(plateA),
                plateB.isEmpty() ? Ingredient.EMPTY : Ingredient.of(plateB), type);
    }

    /**
     * Checks if there is an inscriber recipe that supports the given combination of top/bottom presses. Both the given
     * combination and the reverse will be searched.
     */
    public static boolean isValidOptionalIngredientCombination(Level level, ItemStack pressA, ItemStack pressB) {
        for (InscriberRecipe recipe : getRecipes(level)) {
            if (recipe.getTopOptional().test(pressA) && recipe.getBottomOptional().test(pressB)
                    || recipe.getTopOptional().test(pressB) && recipe.getBottomOptional().test(pressA)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if there is an inscriber recipe that would use the given item stack as an optional ingredient. Bottom and
     * top can be used interchangeably here, because the inscriber will flip the recipe if needed.
     */
    public static boolean isValidOptionalIngredient(Level level, ItemStack is) {
        for (InscriberRecipe recipe : getRecipes(level)) {
            if (recipe.getTopOptional().test(is) || recipe.getBottomOptional().test(is)) {
                return true;
            }
        }

        return false;
    }

}
