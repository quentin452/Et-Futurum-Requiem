package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockModernWoodSlab extends BaseSlab {
	final BlockModernWoodPlanks basePlanks;

	public BlockModernWoodSlab(boolean isDouble) {
		super(isDouble, Material.wood, "crimson", "warped", "mangrove", "cherry", "bamboo");
		basePlanks = (BlockModernWoodPlanks) ModBlocks.WOOD_PLANKS.get();
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
	}

	@Override
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		ModBlocks.WOOD_PLANKS.get().getSubBlocks(p_149666_1_, p_149666_2_, p_149666_3_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return basePlanks.getIcon(p_149691_1_, p_149691_2_ % 8);
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		if (ConfigFunctions.enableExtraBurnableBlocks) {
			int meta = aWorld.getBlockMetadata(aX, aY, aZ) % getTypes().length;
			return meta > 1;
		}
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 20 : 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
	}
}
