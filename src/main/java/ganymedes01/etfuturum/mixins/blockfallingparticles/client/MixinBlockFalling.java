package ganymedes01.etfuturum.mixins.blockfallingparticles.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.fallingdustcolor.IFallingDustColor;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(BlockFalling.class)
public abstract class MixinBlockFalling extends Block implements IFallingDustColor {

	protected MixinBlockFalling(Material p_i45394_1_) {
		super(p_i45394_1_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(16) == 0) {
			if (BlockFalling.func_149831_e(world, x, y - 1, z)) {
				double d0 = (float) x + rand.nextFloat();
				double d1 = (double) y - .05D;
				double d2 = (float) z + rand.nextFloat();
				CustomParticles.spawnFallingDustParticle(world, x, y, z, getDustColor(world.getBlockMetadata(x, y, z)));
			}
		}
	}

	@Override
	public int getDustColor(int meta) {
		if (this == ExternalContent.Blocks.NATURA_HEAT_SAND.get()) {
			return 0xFFD87F33;
		}
		if (this == ExternalContent.Blocks.NETHERLICIOUS_NETHER_GRAVEL.get()) {
			return 0xFF191919;
		}
		if (this == Blocks.gravel || this == ModBlocks.OLD_GRAVEL.get() || this == ExternalContent.Blocks.TCON_GRAVEL_ORE.get()) {
			return 0xFF807C7B;
		}
		return getMapColor(meta).colorValue | 0xFF000000;
	}
}
