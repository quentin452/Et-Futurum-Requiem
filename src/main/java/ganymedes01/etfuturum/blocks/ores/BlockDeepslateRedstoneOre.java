package ganymedes01.etfuturum.blocks.ores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDeepslateRedstoneOre extends BlockDeepslateOre {

	public boolean isLit;

	public BlockDeepslateRedstoneOre(boolean lit) {
		super(lit ? Blocks.lit_redstone_ore : Blocks.redstone_ore);
		if (lit) {
			setCreativeTab(null);
			setBlockName(Utils.getUnlocalisedName("deepslate_lit_redstone_ore"));
			this.isLit = lit;
		}
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_) {
		this.func_150185_e(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_);
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity p_149724_5_) {
		this.func_150185_e(p_149724_1_, p_149724_2_, p_149724_3_, p_149724_4_);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		this.func_150185_e(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_);
		return false;
	}

	private void func_150185_e(World p_150185_1_, int p_150185_2_, int p_150185_3_, int p_150185_4_) {
		this.func_150186_m(p_150185_1_, p_150185_2_, p_150185_3_, p_150185_4_);

		if (this == ModBlocks.DEEPSLATE_REDSTONE_ORE.get()) {
			p_150185_1_.setBlock(p_150185_2_, p_150185_3_, p_150185_4_, ModBlocks.DEEPSLATE_LIT_REDSTONE_ORE.get());
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {
		if (this == ModBlocks.DEEPSLATE_LIT_REDSTONE_ORE.get()) {
			p_149674_1_.setBlock(p_149674_2_, p_149674_3_, p_149674_4_, ModBlocks.DEEPSLATE_REDSTONE_ORE.get());
		}
	}

	private void func_150186_m(World p_150186_1_, int p_150186_2_, int p_150186_3_, int p_150186_4_) {
		Random random = p_150186_1_.rand;
		double d0 = 0.0625D;

		for (int l = 0; l < 6; ++l) {
			double d1 = p_150186_2_ + random.nextFloat();
			double d2 = p_150186_3_ + random.nextFloat();
			double d3 = p_150186_4_ + random.nextFloat();

			if (l == 0 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_ + 1, p_150186_4_).isOpaqueCube()) {
				d2 = p_150186_3_ + 1 + d0;
			}

			if (l == 1 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_ - 1, p_150186_4_).isOpaqueCube()) {
				d2 = p_150186_3_ - d0;
			}

			if (l == 2 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_, p_150186_4_ + 1).isOpaqueCube()) {
				d3 = p_150186_4_ + 1 + d0;
			}

			if (l == 3 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_, p_150186_4_ - 1).isOpaqueCube()) {
				d3 = p_150186_4_ - d0;
			}

			if (l == 4 && !p_150186_1_.getBlock(p_150186_2_ + 1, p_150186_3_, p_150186_4_).isOpaqueCube()) {
				d1 = p_150186_2_ + 1 + d0;
			}

			if (l == 5 && !p_150186_1_.getBlock(p_150186_2_ - 1, p_150186_3_, p_150186_4_).isOpaqueCube()) {
				d1 = p_150186_2_ - d0;
			}

			if (d1 < p_150186_2_ || d1 > p_150186_2_ + 1 || d2 < 0.0D || d2 > p_150186_3_ + 1 || d3 < p_150186_4_ || d3 > p_150186_4_ + 1) {
				p_150186_1_.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected ItemStack createStackedBlock(int p_149644_1_) {
		return ModBlocks.DEEPSLATE_REDSTONE_ORE.newItemStack();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return Item.getItemFromBlock(ModBlocks.DEEPSLATE_REDSTONE_ORE.get());
	}
}
