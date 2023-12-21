package ganymedes01.etfuturum.ducks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public interface ISoulFireInfo {

	default boolean isSoulFire(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block == Blocks.soul_sand || block == ModBlocks.SOUL_SOIL.get() || block == ExternalContent.Blocks.NETHERLICIOUS_SOUL_SOIL.get();
	}

	@SideOnly(Side.CLIENT)
	IIcon getSoulFireIcon(int type);
}
