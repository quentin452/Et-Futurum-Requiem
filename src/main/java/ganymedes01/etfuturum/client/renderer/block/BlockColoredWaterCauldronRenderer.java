package ganymedes01.etfuturum.client.renderer.block;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockPotionCauldron;
import ganymedes01.etfuturum.tileentities.TileEntityCauldronColoredWater;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockColoredWaterCauldronRenderer extends BlockModelBase {

	public BlockColoredWaterCauldronRenderer(int modelID) {
		super(modelID);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileEntityCauldronColoredWater tile = (TileEntityCauldronColoredWater) world.getTileEntity(x, y, z);
		renderer.renderStandardBlock(block, x, y, z);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
		int l = block.colorMultiplier(renderer.blockAccess, x, y, z);
		float f = (float) (l >> 16 & 255) / 255.0F;
		float f1 = (float) (l >> 8 & 255) / 255.0F;
		float f2 = (float) (l & 255) / 255.0F;
		float f4;

		if (EntityRenderer.anaglyphEnable) {
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		tessellator.setColorOpaque_F(f, f1, f2);
		IIcon iicon1 = block.getBlockTextureFromSide(2);
		f4 = 0.125F;
		renderer.renderFaceXPos(block, (double) x - 1.0F + f4, y, z, iicon1);
		renderer.renderFaceXNeg(block, (double) x + 1.0F - f4, y, z, iicon1);
		renderer.renderFaceZPos(block, x, y, (double) z - 1.0F + f4, iicon1);
		renderer.renderFaceZNeg(block, x, y, (double) z + 1.0F - f4, iicon1);
		IIcon iicon2 = BlockCauldron.getCauldronIcon("inner");
		renderer.renderFaceYPos(block, x, (double) y - 1.0F + 0.25F, z, iicon2);
		renderer.renderFaceYNeg(block, x, (double) y + 1.0F - 0.75F, z, iicon2);
		int i1 = renderer.blockAccess.getBlockMetadata(x, y, z);

		int color = tile.getWaterColor();
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		tessellator.setColorOpaque_F(r, g, b);
		IIcon iicon = ((BlockPotionCauldron) ModBlocks.POTION_CAULDRON.get()).grayscaleWaterIcon();
		renderer.renderFaceYPos(block, x, (double) y - 1.0F + BlockCauldron.getRenderLiquidLevel(i1 + 1), z, iicon);

		return true;
	}

}
