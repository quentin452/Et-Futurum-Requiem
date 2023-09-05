package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLantern extends Block {

	public BlockLantern() {
		super(Material.iron);
		float r = 0.0625F;
		float f = 0.375F;
		float f1 = f / 2.0F;
		this.setHarvestLevel("pickaxe", 0);
		this.setStepSound(ConfigSounds.newBlockSounds ? ModSounds.soundLantern : soundTypeMetal);
		this.setHardness(3.5F);
		this.setResistance(3.5F);
		this.setLightLevel(1);
		this.setLightOpacity(500);
		this.setBlockBounds(0.5F - f1, r * 1, 0.5F - f1, 0.5F + f1, r * 7, 0.5F + f1);
		this.setBlockName(Utils.getUnlocalisedName("lantern"));
		this.setBlockTextureName("lantern");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		float r = 0.0625F;
		float f = 0.375F;
		float f1 = f / 2.0F;
		if (p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_) == 0)
			this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, r * 7, 0.5F + f1);
		else
			this.setBlockBounds(0.5F - f1, r * 1, 0.5F - f1, 0.5F + f1, r * 8, 0.5F + f1);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return canPlaceBelow(world, x, y, z) || canPlaceAbove(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return canPlaceBlockAt(world, x, y, z);
	}

	private boolean canPlaceAbove(World world, int x, int y, int z) {
		return world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) || (world.getBlock(x, y + 1, z) == ModBlocks.CHAIN.get() && world.getBlockMetadata(x, y + 1, z) == 0);
	}

	private boolean canPlaceBelow(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z).canPlaceTorchOnTop(world, x, y - 1, z) ||
				World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		switch (side) {
			case 0:
				return 1;
			case 2:
			case 3:
			case 4:
			case 5:
				return canPlaceBelow(world, x, y, z) ? 0 : 1;
			case 1:
			default:
				return 0;
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		this.onNeighborBlockChange(world, x, y, z, block);
	}

//	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
//	{
//		super.onNeighborBlockChange(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
//		if(!canBlockStay(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_)) {
//			setLanternToAir(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
//		}
//	}

	public void setLanternToAir(World world, int x, int y, int z) {
		if (!world.isRemote) {
			this.dropBlockAsItem(world, x, y, z, 0, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName()
	{
		return "lantern";
	}
	
	@Override
	public int getRenderType()
	{
		return RenderIDs.LANTERN;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

}
