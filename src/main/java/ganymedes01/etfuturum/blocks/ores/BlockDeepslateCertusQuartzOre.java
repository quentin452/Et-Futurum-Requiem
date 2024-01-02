package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.ExternalContent;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDeepslateCertusQuartzOre extends BaseSubtypesBlock implements IEmissiveLayerBlock {

	public BlockDeepslateCertusQuartzOre() {
		super(Material.rock, "deepslate_certus_quartz_ore", "deepslate_charged_certus_quartz_ore");
		setBlockSound(ModSounds.soundDeepslate);
		this.setResistance(5.0F);
		setHardness(4.5F);
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().quantityDropped(p_149745_1_);
	}

	@Override
	public int quantityDroppedWithBonus(int p_149679_1_, Random p_149679_2_) {
		return ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().quantityDroppedWithBonus(p_149679_1_, p_149679_2_);
	}

	@Override
	public int damageDropped(int meta) {
		return meta == 0 ? ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().damageDropped(meta)
				: ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get().damageDropped(meta);
	}

	@Override
	public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_) {
		if (p_149690_5_ == 0) {
			ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().dropBlockAsItemWithChance(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_6_, p_149690_7_);
		} else {
			ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get().dropBlockAsItemWithChance(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_6_, p_149690_7_);
		}
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return p_149650_3_ == 0 ? ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().getItemDropped(p_149650_1_, p_149650_2_, p_149650_3_)
				: ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get().getItemDropped(p_149650_1_, p_149650_2_, p_149650_3_);
	}

	@Override
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
		if (p_149734_1_.getBlockMetadata(p_149734_2_, p_149734_3_, p_149734_4_) == 1) {
			ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get().randomDisplayTick(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_, p_149734_5_);
		}
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	@Override
	public IIcon getEmissiveLayerIcon(int side, int meta) {
		return meta == 0 ? ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get().getIcon(side, meta)
				: ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get().getIcon(side, meta);
	}

	//Not sure how to make this work rn
	@Override
	public int getEmissiveMinBrightness(IBlockAccess world, int x, int y, int z) {
		return 1;
	}

	@Override
	public int getRenderType() {
		return RenderIDs.EMISSIVE_DOUBLE_LAYER;
	}
}
