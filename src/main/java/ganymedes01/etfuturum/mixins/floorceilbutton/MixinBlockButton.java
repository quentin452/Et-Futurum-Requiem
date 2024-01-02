package ganymedes01.etfuturum.mixins.floorceilbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraftforge.common.util.ForgeDirection.*;

/**
 * Basically we just add a few extra checks for up buttons (meta 0/8) and down buttons (meta 5/13)
 * Should affect most modded buttons. This could possibly be done with less overrides but I didn't know the mixin context to splice my new code in the manner needed...
 *
 * @author roadhog360
 */
@Mixin(BlockButton.class)
public class MixinBlockButton extends Block {

	protected MixinBlockButton(Material p_i45394_1_) {
		super(p_i45394_1_);
	}

	@Shadow
	private boolean func_150044_m(World p_150044_1_, int p_150044_2_, int p_150044_3_, int p_150044_4_) {
		return false;
	}

	@Shadow
	public int isProvidingWeakPower(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_) {
		return 0;
	}

	/**
	 * Modified this to use a ForgeDirection iterator, iterating through the states when possible looks a little neater.
	 * Also I needed to modify this anyways to add the up/down button check so may as well.
	 */
	@Overwrite
	public boolean canPlaceBlockOnSide(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_) {
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (ForgeDirection.getOrientation(p_149707_5_) == dir && p_149707_1_.isSideSolid(p_149707_2_ - dir.offsetX, p_149707_3_ - dir.offsetY, p_149707_4_ - dir.offsetZ, dir)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Modified this to use a ForgeDirection iterator, iterating through the states when possible looks a little neater.
	 * Also I needed to modify this anyways to add the up/down button check so may as well.
	 */
	@Overwrite
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (p_149742_1_.isSideSolid(p_149742_2_ - dir.offsetX, p_149742_3_ - dir.offsetY, p_149742_4_ - dir.offsetZ, dir)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Modified this to use a ForgeDirection iterator, iterating through the states when possible looks a little neater.
	 * Also I needed to modify this anyways to add the up/down button check so may as well.
	 * Additionally removed random dead '&= 7' operation the j1 local had run on it, seemingly doing nothing at all before being overwritten.
	 */
	@Overwrite
	public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
		int k1 = p_149660_1_.getBlockMetadata(p_149660_2_, p_149660_3_, p_149660_4_) & 8;
		int j1 = 0;

		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir == ForgeDirection.getOrientation(p_149660_5_) && p_149660_1_.isSideSolid(p_149660_2_ - dir.offsetX, p_149660_3_ - dir.offsetY, p_149660_4_ - dir.offsetZ, dir)) {
				switch (dir) {
					case NORTH:
						j1 = 4;
						break;
					case SOUTH:
						j1 = 3;
						break;
					case WEST:
						j1 = 2;
						break;
					case EAST:
						j1 = 1;
						break;
					case UP:
						j1 = 5;
						break;
					case DOWN:
						j1 = 0;
						break;
					default:
						j1 = this.func_150045_e(p_149660_1_, p_149660_2_, p_149660_3_, p_149660_4_);
						break;
				}
			}
		}

		return j1 + k1;
	}

	@Overwrite
	private int func_150045_e(World p_150045_1_, int p_150045_2_, int p_150045_3_, int p_150045_4_) {
		if (p_150045_1_.isSideSolid(p_150045_2_ - 1, p_150045_3_, p_150045_4_, EAST)) return 1;
		if (p_150045_1_.isSideSolid(p_150045_2_ + 1, p_150045_3_, p_150045_4_, WEST)) return 2;
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_, p_150045_4_ - 1, SOUTH)) return 3;
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_, p_150045_4_ + 1, NORTH)) return 4;
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_ - 1, p_150045_4_, UP)) return 5;
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_ + 1, p_150045_4_, DOWN)) return 0;
		return 1;
	}

	@Overwrite
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
		if (this.func_150044_m(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_)) {
			int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_) & 7;
			boolean flag = !p_149695_1_.isSideSolid(p_149695_2_ - 1, p_149695_3_, p_149695_4_, EAST) && l == 1;

			if (!p_149695_1_.isSideSolid(p_149695_2_ + 1, p_149695_3_, p_149695_4_, WEST) && l == 2) {
				flag = true;
			}

			if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ - 1, SOUTH) && l == 3) {
				flag = true;
			}

			if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_, p_149695_4_ + 1, NORTH) && l == 4) {
				flag = true;
			}

			if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_ - 1, p_149695_4_, UP) && l == 5) {
				flag = true;
			}

			if (!p_149695_1_.isSideSolid(p_149695_2_, p_149695_3_ + 1, p_149695_4_, DOWN) && l == 0) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
				p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
			}
		}
	}

	/**
	 * Add hitboxex for new up/down buttons (up uses 0/8 and down uses 5/13
	 *
	 * @param p_150043_1_
	 */
	@Overwrite
	private void func_150043_b(int p_150043_1_) {
		int j = p_150043_1_ & 7;
		boolean flag = (p_150043_1_ & 8) > 0;
		float f = 0.375F;
		float f1 = 0.625F;
		float f2 = 0.1875F;
		float f3 = 0.125F;

		if (flag) {
			f3 = 0.0625F;
		}

		if (j == 1) {
			this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
		} else if (j == 2) {
			this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
		} else if (j == 3) {
			this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
		} else if (j == 4) {
			this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
		} else if (j == 5) {
			this.setBlockBounds(0.5F - f2, 0.0F, f, 0.5F + f2, f3, f1);
		} else if (j == 0) {
			this.setBlockBounds(0.5F - f2, 1.0F - f3, f, 0.5F + f2, 1.0F, f1);
		}
	}

	@Overwrite
	private void func_150042_a(World p_150042_1_, int p_150042_2_, int p_150042_3_, int p_150042_4_, int p_150042_5_) {
		p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_, p_150042_3_, p_150042_4_, this);

		if (p_150042_5_ == 1) {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_ - 1, p_150042_3_, p_150042_4_, this);
		} else if (p_150042_5_ == 2) {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_ + 1, p_150042_3_, p_150042_4_, this);
		} else if (p_150042_5_ == 3) {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_, p_150042_3_, p_150042_4_ - 1, this);
		} else if (p_150042_5_ == 4) {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_, p_150042_3_, p_150042_4_ + 1, this);
		} else if (p_150042_5_ == 0) {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_, p_150042_3_ + 1, p_150042_4_, this);
		} else {
			p_150042_1_.notifyBlocksOfNeighborChange(p_150042_2_, p_150042_3_ - 1, p_150042_4_, this);
		}
	}


	/*
	 * Replaced this function to just call isProvidingWeakPower since it sets the power to 15 if the button is pressed, which is what we want.
	 * The code that was originally here seemed to have a bunch of checks that are seemingly unnecessary since meta > 7 = pressed...?
	 */
	@Overwrite
	public int isProvidingStrongPower(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_) {
		return this.isProvidingWeakPower(p_149748_1_, p_149748_2_, p_149748_3_, p_149748_4_, p_149748_5_);
	}
}
