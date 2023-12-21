package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockAzaleaLeaves extends BaseLeaves {

	public BlockAzaleaLeaves() {
		super("azalea", "flowering_azalea");
		Utils.setBlockSound(this, ModSounds.soundAzaleaLeaves);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ModBlocks.AZALEA.getItem();
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_) {
		return 0xFFFFFF;
	}

	public int getRenderColor(int p_149741_1_) {
		return 0xFFFFFF;
	}
}
