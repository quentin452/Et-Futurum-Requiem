package ganymedes01.etfuturum.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModBlocks.ISubBlocksBlock;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.IRegistryName;
import ganymedes01.etfuturum.items.block.ItemDyedBed;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDyedBed extends BlockBed implements IConfigurable, ISubBlocksBlock, IRegistryName {

	private static final String[] dye_names = new String[] { "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	
	public BlockDyedBed(int dye) {
		super();
		if(dye == 14) {
			dye = 15;
		}
		setHardness(0.2F);
		String dyeName = ModRecipes.dye_names[dye];
		setBlockName(Utils.getUnlocalisedName(dyeName + "_bed"));
		disableStats();
		setBlockTextureName(dyeName + "_bed");
		setCreativeTab(isEnabled() ? EtFuturum.creativeTabBlocks : null);
	}
	
//	@SideOnly(Side.CLIENT)
//	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
//	{
//		if(this == ModBlocks.beds[14] && p_149666_2_ != null) {
//			p_149666_3_.add(new ItemStack(Items.bed, 1, 0));
//		}
//		super.getSubBlocks(p_149666_1_, p_149666_2_, p_149666_3_);
//	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		if (!entity.isSneaking() && ConfigMixins.bouncyBeds) {
			entity.fallDistance /= 2;
			if (entity.motionY < 0)
				entity.getEntityData().setDouble(Reference.MOD_ID + ":bed_bounce", -entity.motionY * 0.66);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(ConfigMixins.bouncyBeds) {
			NBTTagCompound data = entity.getEntityData();
			if (data.hasKey(Reference.MOD_ID + ":bed_bounce")) {
				entity.motionY = data.getDouble(Reference.MOD_ID + ":bed_bounce");
				data.removeTag(Reference.MOD_ID + ":bed_bounce");
			}
		}
	}

    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player)
    {
    	return true;
    }
    
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return isBlockHeadOfBed(p_149650_1_) ? Item.getItemById(0) : Item.getItemFromBlock(this);
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
	{
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public String getItemIconName() {
		return "bed_" + getTextureName().replace("_bed", "");
	}
	
	@Override
	public boolean isEnabled() {
		return ConfigBlocksItems.enableDyedBeds;
	}

	@Override
	public Class<? extends ItemBlock> getItemBlockClass() {
		// TODO Auto-generated method stub
		return ItemDyedBed.class;
	}

	@Override
	public String getRegistryName() {
		String name = getUnlocalizedName();
		String[] strings = name.split("\\.");
		return this == ModBlocks.beds[8] ? "light_gray_bed" : strings[strings.length - 1];
	}
	
}
