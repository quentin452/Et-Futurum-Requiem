package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenCherryTrees;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.List;
import java.util.Random;

public class BlockModernSapling extends BlockSapling implements ISubBlocksBlock {
	private final String[] types = new String[]{"mangrove_propagule", "cherry_sapling"};
	private final IIcon[] icons = new IIcon[types.length];

	public BlockModernSapling() {
		setStepSound(Block.soundTypeGrass);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		if (ConfigBlocksItems.enableMangroveBlocks) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
		}
		if (ConfigBlocksItems.enableCherryBlocks) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return icons[(p_149691_2_ & 7) % icons.length];
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		for (int i = 0; i < icons.length; ++i) {
			icons[i] = p_149651_1_.registerIcon(types[i]);
		}
	}

	private static final WorldGenAbstractTree cherry = new WorldGenCherryTrees(true);

	public void func_149878_d(World p_149878_1_, int p_149878_2_, int p_149878_3_, int p_149878_4_, Random p_149878_5_) {
		if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_)) {
			return;
		}

		int l = p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_) & 7;
		WorldGenAbstractTree tree = null;

		switch (l) {
			case 1:
				if (ConfigBlocksItems.enableCherryBlocks) {
					tree = cherry;
				}
				break;
		}

		if (tree != null) {
			Block block = p_149878_1_.getBlock(p_149878_2_, p_149878_3_, p_149878_4_);
			int meta = p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_);
			p_149878_1_.setBlock(p_149878_2_, p_149878_3_, p_149878_4_, Blocks.air);
			boolean success = tree.generate(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_);
			if (!success) {
				p_149878_1_.setBlock(p_149878_2_, p_149878_3_, p_149878_4_, block, meta, 2);
			}
		}
	}

	@Override
	public IIcon[] getIcons() {
		return icons;
	}

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return types[stack.getItemDamage() % types.length];
	}
}
