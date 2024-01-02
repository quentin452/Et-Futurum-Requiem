package ganymedes01.etfuturum.items.equipment;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.BaseUninflammableItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEFRHoe extends ItemHoe {

	public ItemEFRHoe(ToolMaterial material, int durabilityOverride) {
		super(material);
		this.setMaxDamage(durabilityOverride > -1 ? durabilityOverride : material.getMaxUses());
		this.setUnlocalizedName(Utils.getUnlocalisedName("netherite_hoe"));
		this.setTextureName("netherite_hoe");
		this.setCreativeTab(EtFuturum.creativeTabItems);
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
		return ModItems.NETHERITE_INGOT.get() == par2ItemStack.getItem() || super.getIsRepairable(par1ItemStack, par2ItemStack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return getUnlocalizedName().contains("netherite") && !ConfigFunctions.enableNetheriteFlammable;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		if (!getUnlocalizedName().contains("netherite"))
			return null;
		return BaseUninflammableItem.createUninflammableItem(world, location);
	}

}
