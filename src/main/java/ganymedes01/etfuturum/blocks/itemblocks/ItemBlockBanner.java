package ganymedes01.etfuturum.blocks.itemblocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.lib.EnumColour;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.tileentities.TileEntityBanner;
import ganymedes01.etfuturum.tileentities.TileEntityBanner.EnumBannerPattern;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockBanner extends ItemBlock {

	public ItemBlockBanner(Block block) {
		super(block);
		setMaxDamage(0);
		setMaxStackSize(16);
		setHasSubtypes(true);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		final Block block = world.getBlock(x, y, z);
		if (block == Blocks.cauldron) {
			final int meta = world.getBlockMetadata(x, y, z);
			if (meta > 0) {
				stack.setTagCompound(null);
				world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3);
				return true;
			}
		}

		if (side == 0) {
			return false;
		} else if (!block.getMaterial().isSolid()) {
			return false;
		} else {
			switch (side) {
				case 1:
					y++;
					break;
				case 2:
					z--;
					break;
				case 3:
					z++;
					break;
				case 4:
					x--;
					break;
				case 5:
					x++;
					break;
			}

			if (!player.canPlayerEdit(x, y, z, side, stack)) {
				return false;
			} else if (!field_150939_a.canPlaceBlockAt(world, x, y, z)) {
				return false;
			} else {
				if (side == 1) {
					int meta = MathHelper.floor_double((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
					world.setBlock(x, y, z, field_150939_a, meta, 3);
				} else {
					world.setBlock(x, y, z, field_150939_a, side, 3);
				}

				world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);
				stack.stackSize--;

				TileEntityBanner banner = (TileEntityBanner) world.getTileEntity(x, y, z);
				if (banner != null) {
					banner.isStanding = side == 1;
					banner.setItemValues(stack);
				}
				return true;
			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return StatCollector.translateToLocal("item.banner." + ModRecipes.dye_names[getBaseColor(stack).getDamage()] + ".name");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		NBTTagCompound nbttagcompound = getSubTag(stack, "BlockEntityTag", false);

		if (nbttagcompound != null && nbttagcompound.hasKey("Patterns")) {
			NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);

			for (int i = 0; i < nbttaglist.tagCount() && i < 6; i++) {
				NBTTagCompound nbt = nbttaglist.getCompoundTagAt(i);
				EnumColour color = EnumColour.fromDamage(nbt.getInteger("Color"));
				EnumBannerPattern pattern = EnumBannerPattern.getPatternByID(nbt.getString("Pattern"));

				if (pattern != null) {
					tooltip.add(StatCollector.translateToLocal("item.banner." + pattern.getPatternName() + "." + ModRecipes.dye_names[color.getDamage()]));
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 0) {
			return 0xFFFFFF;
		}
		EnumColour EnumColour = getBaseColor(stack);
		return EnumColour.getRGB();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List subItems) {
		for (int i = 0; i < 16; i++) {
			subItems.add(new ItemStack(item, 1, i));
		}
	}

	private EnumColour getBaseColor(ItemStack stack) {
		NBTTagCompound nbttagcompound = getSubTag(stack, "BlockEntityTag", false);
		EnumColour color;

		if (nbttagcompound != null && nbttagcompound.hasKey("Base")) {
			color = EnumColour.fromDamage(nbttagcompound.getInteger("Base"));
		} else {
			color = EnumColour.fromDamage(stack.getItemDamage());
		}

		return color;
	}

	public static NBTTagCompound getSubTag(ItemStack stack, String key, boolean create) {
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey(key, 10)) {
			return stack.stackTagCompound.getCompoundTag(key);
		} else if (create) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			stack.setTagInfo(key, nbttagcompound);
			return nbttagcompound;
		} else {
			return null;
		}
	}

}
