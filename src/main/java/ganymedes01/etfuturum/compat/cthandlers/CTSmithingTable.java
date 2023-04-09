package ganymedes01.etfuturum.compat.cthandlers;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.recipes.SmithingTableRecipes;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.recipes.ICraftingRecipe;
import minetweaker.api.recipes.IRecipeAction;
import minetweaker.api.recipes.IRecipeFunction;
import minetweaker.api.recipes.ShapedRecipe;
import minetweaker.mc1710.item.MCItemStack;
import minetweaker.mc1710.recipes.*;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import scala.actors.migration.pattern;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import sun.awt.HToolkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/** Remember smithing table recipes are output, THEN input, not input then output
 * mods.etfuturum.smithingTable.addRecipe(<IC2:itemToolBronzeSword>, <minecraft:iron_sword>, <ore:ingotBronze>);
 * mods.etfuturum.smithingTable.addRecipe(<minecraft:stone_sword>, <minecraft:wooden_sword>, <minecraft:cobblestone>);
 *
 * ALSO THIS CODE IS VERY SCUFFED I HAVE NO IDEA WHAT I AM DOING, CRAFTTWEAKER SUPPORT IS CONFUSING BUT NOBODY WOULD STOP ASKING FOR IT
 * SO WARNING IF BAD CODE MAKES YOU CRINGE
 */

@ZenClass("mods.etfuturum.smithingTable")
public class CTSmithingTable {

    private static final List<ICraftingRecipe> transformerRecipes = new ArrayList<>();

    @ZenMethod
    public static void remove(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Removal output cannot be null");

        List<IRecipe> recipes = SmithingTableRecipes.getInstance().getRecipes();
        List<IRecipe> toRemove = new ArrayList<>();

        for(IRecipe recipe : recipes) {
            if(output.matches(new MCItemStack(recipe.getRecipeOutput()))) {
                toRemove.add(recipe);
            }
        }

        if (toRemove.isEmpty()) {
            MineTweakerAPI.logWarning("No smithing table recipes for " + output.toString());
        } else {
            MineTweakerAPI.apply(new RemoveAction(toRemove));
        }
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient toolSlot, IIngredient ingredient, IRecipeFunction function, IRecipeAction action) {
        addRecipe(toolSlot, ingredient, output, true, function, action);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient toolSlot, IIngredient ingredient) {
        addRecipe(toolSlot, ingredient, output, true, null, null);
    }

    @ZenMethod
    public static void addRecipeNoNBT(IItemStack output, IIngredient toolSlot, IIngredient ingredient, IRecipeFunction function, IRecipeAction action) {
        addRecipe(toolSlot, ingredient, output, false, function, action);
    }

    @ZenMethod
    public static void addRecipeNoNBT(IItemStack output, IIngredient toolSlot, IIngredient ingredient) {
        addRecipe(toolSlot, ingredient, output, false, null, null);
    }

    private static void addRecipe(IIngredient toolSlot, IIngredient ingredientSlot, IItemStack output, boolean copyNBT, IRecipeFunction function, IRecipeAction action) {
        if(toolSlot == null || ingredientSlot == null || output == null) {
            MineTweakerAPI.logError("One or more smithing table ingredients were invalid or null");
        } else {
            ShapedRecipe recipe = new ShapedRecipe(output, new IIngredient[][] {{toolSlot, ingredientSlot}}, function, action, false);
            IRecipe irecipe = instance.convert(recipe, copyNBT); //I have to do it this way, I cannot make this function static, because I need to declare objects in it.
            MineTweakerAPI.apply(new ActionAddRecipe(irecipe, recipe));
        }
    }


    private static final CTSmithingTable instance = new CTSmithingTable();
    private final Method getRecipeType = ReflectionHelper.findMethod(RecipeConverter.class, null, new String[] {"getRecipeType"}, Integer.class);
    private IRecipe convert(ShapedRecipe recipe, boolean copyNBT) {
        IIngredient[] ingredients = recipe.getIngredients();
        byte[] posx = recipe.getIngredientsX();
        byte[] posy = recipe.getIngredientsY();
        int type = 0;
        try {
            type = (int) getRecipeType.invoke(null, ingredients);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        int counter;
        if (type == 2) {
            ItemStack[] basicIngredients = new ItemStack[recipe.getHeight() * recipe.getWidth()];

            for(counter = 0; counter < ingredients.length; ++counter) {
                basicIngredients[posx[counter] + posy[counter] * recipe.getWidth()] = MineTweakerMC.getItemStack(ingredients[counter]);
            }

            return new CraftTweakerSmithingRecipeBasic(basicIngredients, recipe, copyNBT);
        } else if (type != 1) {
            return new CraftTweakerSmithingRecipeAdvanced(recipe, copyNBT);
        } else {
            Object[] converted = new Object[recipe.getHeight() * recipe.getWidth()];

            for(counter = 0; counter < ingredients.length; ++counter) {
                converted[posx[counter] + posy[counter] * recipe.getWidth()] = ingredients[counter].getInternal();
            }

            counter = 0;
            String[] parts = new String[recipe.getHeight()];
            ArrayList<Object> rarguments = new ArrayList<>();

            for(int i = 0; i < recipe.getHeight(); ++i) {
                char[] pattern = new char[recipe.getWidth()];

                for(int j = 0; j < recipe.getWidth(); ++j) {
                    int off = i * recipe.getWidth() + j;
                    if (converted[off] == null) {
                        pattern[j] = ' ';
                    } else {
                        pattern[j] = (char)(65 + counter);
                        rarguments.add(pattern[j]);
                        rarguments.add(converted[off]);
                        ++counter;
                    }
                }

                parts[i] = new String(pattern);
            }

            rarguments.addAll(0, Arrays.asList(parts));
            return new CraftTweakerSmithingRecipe(rarguments.toArray(), recipe, copyNBT);
        }
    }

    // ######################
    // ### Action classes ###
    // ######################

    private static class RemoveAction implements IUndoableAction {

        List<IRecipe> recipes;

        public RemoveAction(List<IRecipe> recipes) {
            this.recipes = recipes;
        }

        @Override
        public void apply() {
            SmithingTableRecipes.getInstance().getRecipes().removeAll(recipes);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            SmithingTableRecipes.getInstance().getRecipes().addAll(recipes);
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " smithing table recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + recipes.size() + " smithing table recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    private static class ActionAddRecipe implements IUndoableAction {
        private final IRecipe recipe;
        private final ICraftingRecipe craftingRecipe;

        public ActionAddRecipe(IRecipe recipe, ICraftingRecipe craftingRecipe) {
            this.recipe = recipe;
            this.craftingRecipe = craftingRecipe;
        }

        public void apply() {
            SmithingTableRecipes.getInstance().addRecipe(recipe);
            if (this.craftingRecipe.hasTransformers()) {
                transformerRecipes.add(this.craftingRecipe);
            }

        }

        public boolean canUndo() {
            return true;
        }

        public void undo() {
            SmithingTableRecipes.getInstance().getRecipes().remove(this.recipe);
            if (this.craftingRecipe.hasTransformers()) {
                transformerRecipes.remove(this.craftingRecipe);
            }

        }

        public String describe() {
            return "Adding recipe for " + this.recipe.getRecipeOutput().getDisplayName();
        }

        public String describeUndo() {
            return "Removing recipe for " + this.recipe.getRecipeOutput().getDisplayName();
        }

        public Object getOverrideKey() {
            return null;
        }
    }

    private class CraftTweakerSmithingRecipe extends ShapedRecipeOre implements SmithingTableRecipes.ISmithingRecipe {

        private final boolean copyNBT;

        public CraftTweakerSmithingRecipe(Object[] contents, ShapedRecipe recipe, boolean copyNBT) {
            super(contents, recipe);
            this.copyNBT = copyNBT;
        }

        @Override
        public boolean copiesNBT() {
            return copyNBT;
        }
    }

    private class CraftTweakerSmithingRecipeBasic extends ShapedRecipeBasic implements SmithingTableRecipes.ISmithingRecipe {

        private final boolean copyNBT;

        public CraftTweakerSmithingRecipeBasic(ItemStack[] basicInputs, ShapedRecipe recipe, boolean copyNBT) {
            super(basicInputs, recipe);
            this.copyNBT = copyNBT;
        }

        @Override
        public boolean copiesNBT() {
            return copyNBT;
        }
    }

    private class CraftTweakerSmithingRecipeAdvanced extends ShapedRecipeAdvanced implements SmithingTableRecipes.ISmithingRecipe {

        //For some reason I had to implement all the functions but... how? This class is not abstract...

        private final ShapedRecipe recipe;
        private final boolean copyNBT;

        public CraftTweakerSmithingRecipeAdvanced(ShapedRecipe recipe, boolean copyNBT) {
            super(recipe);
            this.copyNBT = copyNBT;
            this.recipe = recipe;
        }

        @Override
        public boolean copiesNBT() {
            return copyNBT;
        }

        public boolean matches(InventoryCrafting inventory, World world) {
            return this.recipe.matches(MCCraftingInventory.get(inventory));
        }

        public ItemStack getCraftingResult(InventoryCrafting inventory) {
            return MineTweakerMC.getItemStack(this.recipe.getCraftingResult(MCCraftingInventory.get(inventory))).copy();
        }

        public int getRecipeSize() {
            return 2;
        }

        public ItemStack getRecipeOutput() {
            return MineTweakerMC.getItemStack(this.recipe.getOutput());
        }
    }
}