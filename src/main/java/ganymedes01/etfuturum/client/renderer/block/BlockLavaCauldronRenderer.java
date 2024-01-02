package ganymedes01.etfuturum.client.renderer.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockLavaCauldronRenderer extends BlockModelBase {

	public BlockLavaCauldronRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderBlockCauldron(Blocks.cauldron, x, y, z);
		IIcon lava = BlockLiquid.getLiquidIcon("lava_still");
		renderer.renderFaceYPos(block, x, y - 1.0F + (0.9375F), z, lava);
		return true;
	}

}
