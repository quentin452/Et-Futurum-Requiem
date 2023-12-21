package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPinkPetals extends BaseFlower implements IGrowable {

	private IIcon stemIcon;

	public BlockPinkPetals() {
		super();
		setNames("pink_petals");
		Utils.setBlockSound(this, ModSounds.soundPinkPetals);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = new IconFlipped(reg.registerIcon(getTextureName()), true, false);
		stemIcon = reg.registerIcon(getTextureName() + "_stem");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 7 ? stemIcon : blockIcon;
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		int ordinal = MathHelper.floor_double((double) (p_149689_5_.rotationYaw / 90.0F) + 0.5D) & 3;
		switch (ordinal) {
			case 1:
				ordinal = 4;
				break;
			case 2:
				ordinal = 12;
				break;
			case 3:
				ordinal = 8;
				break;
		}
		p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, ordinal, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		if (player.getHeldItem() != null && player.getHeldItem().getItem() == Item.getItemFromBlock(this)) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta % 4 < 3) {
				world.playSound((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, stepSound.func_150496_b(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F, false);
				world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
				return true;
			}
		}
		return false;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return (meta % 4) + 1;
	}

	@Override
	public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_) {
		return 0;
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return 0;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_) {
		setBlockBounds(0, 0, 0, 1, 0.1875F, 1);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_) {
		return AxisAlignedBB.getBoundingBox(p_149633_2_, p_149633_3_, p_149633_4_, 1 + p_149633_2_, 0.1875 + p_149633_3_, 1 + p_149633_4_);
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}

	@Override
	public int getRenderType() {
		return RenderIDs.PINK_PETALS;
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean p_149851_5_) {
		return true;
	}

	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z) {
		return true;
	}

	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta % 4 < 3) {
			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
		} else {
			dropBlockAsItem(world, x, y, z, new ItemStack(getItemDropped(meta, random, 0), 1, quantityDropped(meta, 0, random)));
		}
	}
}
