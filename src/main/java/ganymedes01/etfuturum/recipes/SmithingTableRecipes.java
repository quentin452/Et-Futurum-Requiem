package ganymedes01.etfuturum.recipes;

import java.util.*;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class SmithingTableRecipes {

	private static final SmithingTableRecipes instance = new SmithingTableRecipes();
	private final List<IRecipe> recipes = new ArrayList<>();

	public static SmithingTableRecipes getInstance()
	{
		return instance;
	}

	public static void init() {
		if(ConfigBlocksItems.enableNetherite) {
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_pickaxe), "ingotNetherite", new ItemStack(Items.diamond_pickaxe, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_axe), "ingotNetherite", new ItemStack(Items.diamond_axe, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_spade), "ingotNetherite", new ItemStack(Items.diamond_shovel, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_sword), "ingotNetherite", new ItemStack(Items.diamond_sword, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_hoe), "ingotNetherite", new ItemStack(Items.diamond_hoe, 1, OreDictionary.WILDCARD_VALUE));

			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_helmet), "ingotNetherite", new ItemStack(Items.diamond_helmet, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_chestplate), "ingotNetherite", new ItemStack(Items.diamond_chestplate, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_leggings), "ingotNetherite", new ItemStack(Items.diamond_leggings, 1, OreDictionary.WILDCARD_VALUE));
			SmithingTableRecipes.getInstance().addRecipe(new ItemStack(ModItems.netherite_boots), "ingotNetherite", new ItemStack(Items.diamond_boots, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

	public IRecipe addRecipe(ItemStack result, Object materialSlot, Object toolSlot)
	{
		return addRecipe(result, materialSlot, toolSlot, true);
	}

	public IRecipe addRecipe(ItemStack result, Object materialSlot, Object toolSlot, boolean copyNBT)
	{
		return addRecipe(new SmithingRecipe(result, materialSlot, toolSlot, copyNBT));
	}

	/**
	 * List of IRecipes for the smithing table. By default this list contains custom instances of Forge's ShapedOreRecipe, and CraftTweaker's ShapedRecipeOre.
	 * Other mods may add custom classes for recipes like in vanilla though. The smithing table otherwise works like a vanilla crafting table with two slots.
	 * If you add a recipe to this list, its size should be 2. An exception will be thrown if it isn't.
	 */
	public IRecipe addRecipe(IRecipe recipe)
	{
		if(recipe.getRecipeSize() != 2) {
			throw new IllegalArgumentException("Smithing table recipe size was not 2.");
		}
		instance.recipes.add(recipe);
		return recipe;
	}

	/**
	 * Do not use this to add recipes, use addRecipe.
	 */
	public List<IRecipe> getRecipes() {
		return recipes;
	}

	public ItemStack findMatchingRecipe(InventoryCrafting inv, World world)
	{
		for (IRecipe recipe : recipes) {
			if (recipe.matches(inv, world)) {
				ItemStack stack = recipe.getCraftingResult(inv);
				if(recipe instanceof ISmithingRecipe && ((ISmithingRecipe) recipe).copiesNBT()) { //The value of the map is if this recipe is set to copy NBT from the tool slot.
					ItemStack toCopy = inv.getStackInSlot(0);
					if(toCopy.hasTagCompound()) {
						stack.setTagCompound(toCopy.getTagCompound());
					}
					if(toCopy.isItemStackDamageable() && stack.isItemStackDamageable()) {
						stack.setItemDamage(toCopy.getItemDamage());
					}
				}
				return stack;
			}
		}

		return null;
	}

	/**
	 * Just used by default smithing recipes so I don't have to copy the code for smithing outputs copying NBT from the tool slot to the CraftTweaker version of the recipe class.
	 * If you use a custom smithing recipe class you do not need to use this, you can just override getCraftingResult in an IRecipe.
	 */
	public interface ISmithingRecipe {
		abstract boolean copiesNBT();
	}

	public class SmithingRecipe extends ShapedOreRecipe implements ISmithingRecipe {

		private final boolean copyNBT;

		public SmithingRecipe(ItemStack result, Object input, Object material, boolean copyNBT) {
			super(result, "xy", 'x', input, 'y', material);
			this.copyNBT = copyNBT;
		}

		@Override
		public boolean copiesNBT() {
			return copyNBT;
		}
	}
}
