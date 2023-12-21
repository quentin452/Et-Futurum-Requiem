package ganymedes01.etfuturum.mixins.floorceilbutton;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

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
	public int isProvidingWeakPower(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_) {
		return 0;
	}

	@Inject(method = "canPlaceBlockOnSide", at = @At(value = "HEAD"), cancellable = true)
	public void canPlaceBlockUpOrDown(World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if ((dir == UP && world.isSideSolid(x, y - 1, z, UP)) || (dir == DOWN && world.isSideSolid(x, y + 1, z, DOWN))) {
			cir.setReturnValue(true);

		}
	}

	/**
	 * Modified this to use a ForgeDirection iterator, iterating through the states when possible looks a little neater.
	 * Also I needed to modify this anyways to add the up/down button check so may as well.
	 */
	@Inject(method = "canPlaceBlockAt", at = @At(value = "HEAD"), cancellable = true)
	public void canPlaceBlockAtTopOrBottom(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		if ((world.isSideSolid(x, y - 1, z, UP)) || (world.isSideSolid(x, y + 1, z, DOWN))) {
			cir.setReturnValue(true);
		}
	}

	/**
	 * Modified this to use a ForgeDirection iterator, iterating through the states when possible looks a little neater.
	 * Also I needed to modify this anyways to add the up/down button check so may as well.
	 * Additionally removed random dead '&= 7' operation the j1 local had run on it, seemingly doing nothing at all before being overwritten.
	 */
	@Inject(method = "onBlockPlaced",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockButton;func_150045_e(Lnet/minecraft/world/World;III)I"),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void onBlockPlacedUpDown(World world, int x, int y, int z, int side,
									float hitX, float hitY, float hitZ, int var,
									CallbackInfoReturnable<Integer> cir,
									int j1, int k1, ForgeDirection dir) {
		if (dir == UP && world.isSideSolid(x, y - 1, z, UP)) {
			cir.setReturnValue(5 + k1);
		}
		if (dir == DOWN && world.isSideSolid(x, y + 1, z, DOWN)) {
			cir.setReturnValue(k1);
		}
	}

	@Inject(method = "func_150045_e", at = @At(value = "HEAD"), cancellable = true)
	private void func_150045_e_up_down(World p_150045_1_, int p_150045_2_, int p_150045_3_, int p_150045_4_, CallbackInfoReturnable<Integer> cir) {
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_ - 1, p_150045_4_, UP)) {
			cir.setReturnValue(5);
		}
		if (p_150045_1_.isSideSolid(p_150045_2_, p_150045_3_ + 1, p_150045_4_, DOWN)) {
			cir.setReturnValue(0);
		}
	}

//	@Redirect(method = "onNeighborBlockChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSideSolid(IIILnet/minecraftforge/common/util/ForgeDirection;)Z"))
//	public boolean captureVars(World instance, int x, int y, int z, ForgeDirection side) {
//		return false;
//	}

	@Unique
	private World etfuturum$nWorld;
	@Unique
	private int etfuturum$nMeta;
	@Unique
	private int etfuturum$nX;
	@Unique
	private int etfuturum$nY;
	@Unique
	private int etfuturum$nZ;

	@Inject(method = "onNeighborBlockChange",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSideSolid(IIILnet/minecraftforge/common/util/ForgeDirection;)Z", shift = At.Shift.BEFORE, ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void captureVars(World world, int x, int y, int z, Block block, CallbackInfo cir, int l) {
		etfuturum$nWorld = world;
		etfuturum$nMeta = l;
		etfuturum$nX = x;
		etfuturum$nY = y;
		etfuturum$nZ = z;
	}

	@ModifyVariable(method = "onNeighborBlockChange",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSideSolid(IIILnet/minecraftforge/common/util/ForgeDirection;)Z", shift = At.Shift.BEFORE, ordinal = 0))
	public boolean modifyFlag(boolean flag) {
		return flag || (etfuturum$nMeta == 5 && !etfuturum$nWorld.isSideSolid(etfuturum$nX, etfuturum$nY - 1, etfuturum$nZ, ForgeDirection.UP)) ||
				(etfuturum$nMeta == 0 && !etfuturum$nWorld.isSideSolid(etfuturum$nX, etfuturum$nY + 1, etfuturum$nZ, ForgeDirection.DOWN));
	}

	@Inject(method = "func_150043_b", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void func_150043_b(int meta, CallbackInfo ci, int j, boolean flag, float f, float f1, float f2, float f3) {
		if (j == 5) {
			this.setBlockBounds(0.5F - f2, 0.0F, f, 0.5F + f2, f3, f1);
		} else if (j == 0) {
			this.setBlockBounds(0.5F - f2, 1.0F - f3, f, 0.5F + f2, 1.0F, f1);
		}
	}

	@Inject(method = "func_150042_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V", ordinal = 5), cancellable = true)
	private void func_150042_a(World world, int x, int y, int z, int buttonMeta, CallbackInfo ci) {
		if (buttonMeta == 0) {
			//Default is already to do a redstone update below, so we only need to add a check for up.
			//This is also injected into the final "else" in the if chain
			world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
			ci.cancel(); //We return so the redstone update doesn't ALSO fire down.
		}
	}


	/*
	 * Replaced this function to just call isProvidingWeakPower since it sets the power to 15 if the button is pressed, which is what we want.
	 * The code that was originally here seemed to have a bunch of checks that are seemingly unnecessary since meta > 7 = pressed...?
	 * Either way mixing into it is hell and the functionality here seems to already be what we want.
	 * So instead of mixing into ternary operator hell, we just redirect it to the function that already does what we want.
	 */
	@Inject(method = "isProvidingStrongPower", at = @At(value = "HEAD"), cancellable = true)
	public void isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int buttonMeta, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.isProvidingWeakPower(world, x, y, z, buttonMeta));
	}
}
