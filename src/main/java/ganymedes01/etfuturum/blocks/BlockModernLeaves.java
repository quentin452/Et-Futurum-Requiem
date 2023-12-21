package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockModernLeaves extends BaseLeaves {

	public BlockModernLeaves() {
		super("mangrove", "cherry");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		if (ConfigBlocksItems.enableMangroveBlocks) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
		}
		if (ConfigBlocksItems.enableCherryBlocks) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
		}
	}

	@Override
	public int getRange(int meta) {
		return meta == 1 ? 7 : 4;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		if (meta % 4 == 0) {
			return 0;
		}
		return super.quantityDropped(meta, fortune, random);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		if (p_149650_1_ % 4 == 0) {
			return null;
		}
		return ModBlocks.SAPLING.getItem();
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_) {
		return p_149720_1_.getBlockMetadata(p_149720_2_, p_149720_3_, p_149720_4_) % 4 == 0 ? super.colorMultiplier(p_149720_1_, p_149720_2_, p_149720_3_, p_149720_4_) : 0xFFFFFF;
	}

	public int getRenderColor(int p_149741_1_) {
		return p_149741_1_ % 4 == 0 ? 0x92C648 : 0xFFFFFF;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) % 4 == 1) {
			if (FMLClientHandler.instance().getClient().gameSettings.particleSetting == 0) {
				if (world.getBlock(x, y - 1, z).getMaterial() == Material.air && rand.nextInt(10) == 0) {
					CustomParticles.spawnCherryLeaf(world, x + rand.nextFloat(), y, z + rand.nextFloat());
				}
			}
			return;
		}
		super.randomDisplayTick(world, x, y, z, rand);
	}
}
