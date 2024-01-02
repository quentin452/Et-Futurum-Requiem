package ganymedes01.etfuturum.blocks.ores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.ExternalContent;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public class BlockDeepslateThaumcraftOre extends BaseSubtypesBlock implements IEmissiveLayerBlock {

	public static final int[] colors = new int[]{0xFFFFFF, 0xFFFF7E, 0xFF3C01, 0x0090FF, 0x00A000, 0xEECCFF, 0x555577};
	private IIcon infusedOverlay;

	public BlockDeepslateThaumcraftOre() {
		super(Material.rock, "deepslate_cinnabar_ore", "air_infused_deepslate", "fire_infused_deepslate", "water_infused_deepslate", "earth_infused_deepslate",
				"order_infused_deepslate", "entropy_infused_deepslate", "amber_bearing_deepslate");
		setBlockSound(ModSounds.soundDeepslate);
		this.setResistance(5.0F);
		setHardness(2.25F);
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get().addHitEffects(worldObj, target, effectRenderer);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get().addDestroyEffects(world, x, y, z, meta, effectRenderer);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get().getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get().getExpDrop(world, metadata, fortune);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		setIcons(new IIcon[3]);
		getIcons()[0] = reg.registerIcon(Reference.MOD_ID + ":deepslate_cinnabar_ore");
		getIcons()[1] = reg.registerIcon(Reference.MOD_ID + ":infused_deepslate");
		getIcons()[2] = reg.registerIcon(Reference.MOD_ID + ":amber_bearing_deepslate");
		infusedOverlay = reg.registerIcon("thaumcraft:infusedore");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 0)
			return getIcons()[0];
		if (meta == 7)
			return getIcons()[2];
		return getIcons()[1];
	}

	@Override
	public IIcon getEmissiveLayerIcon(int side, int meta) {
		if (meta > 0 && meta < 7) {
			return infusedOverlay;
		}
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public int getEmissiveMinBrightness(IBlockAccess world, int x, int y, int z) {
		return 15;
	}

	@Override
	public int getRenderType() {
		return RenderIDs.EMISSIVE_DOUBLE_LAYER;
	}

	@Override
	public int getEmissiveLayerColor(int meta) {
		return colors[meta % colors.length];
	}

	public boolean isMetaNormalBlock(int meta) {
		return meta == 0 || meta == 7;
	}

	@Override
	public boolean doesEmissiveLayerHaveDirShading(int meta) {
		return false;
	}
}
