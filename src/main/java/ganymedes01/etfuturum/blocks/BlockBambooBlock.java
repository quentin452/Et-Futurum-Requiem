package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockBambooBlock extends BlockModernLog implements ISubBlocksBlock {

	public BlockBambooBlock(String type, SoundType soundBambooWood) {
		super(type, soundBambooWood);
		types = new String[]{type + "_block", "stripped_" + type + "_block"};
		setBlockName(Utils.getUnlocalisedName(type + "_block"));
	}

	@Override
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
		if (ConfigBlocksItems.enableStrippedLogs) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
		}
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		field_150167_a = new IIcon[types.length];
		field_150166_b = new IIcon[types.length];

		blockIcon = field_150167_a[0] = iconRegister.registerIcon(types[0]);
		field_150166_b[0] = iconRegister.registerIcon(types[0] + "_top");

		field_150167_a[1] = iconRegister.registerIcon(types[1]);
		field_150166_b[1] = iconRegister.registerIcon(types[1] + "_top");
	}
}
