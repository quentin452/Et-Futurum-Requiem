package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBambooShoot extends BlockBush implements IGrowable {
	public BlockBambooShoot() {
		super(Material.plants);
		setBlockTextureName("bamboo_stage0");
		Utils.setBlockSound(this, ModSounds.soundBambooSapling);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextInt(3) == 0 && world.isAirBlock(x, y + 1, z) && world.getBlockLightValue(x, y, z) >= 9) {
			world.setBlock(x, y, z, ModBlocks.BAMBOO.get(), 0, 3);
			world.setBlock(x, y + 1, z, ModBlocks.BAMBOO.get(), 2, 3);
		}
	}

	@Override
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return ModItems.BAMBOO.get();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ModItems.BAMBOO.get();
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean p_149851_5_) {
		return world.isAirBlock(x, y + 1, z);
	}

	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		return true;
	}

	@Override
	public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_) {
		updateTick(p_149853_1_, p_149853_3_, p_149853_4_, p_149853_5_, p_149853_2_);
	}
}
