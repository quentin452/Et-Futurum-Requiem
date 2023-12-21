package ganymedes01.etfuturum.mixins.uninflammableitem;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {

	@Shadow
	public abstract ItemStack getEntityItem();

	public MixinEntityItem(World p_i1582_1_) {
		super(p_i1582_1_);
	}

	private void makeImmuneToFire(ItemStack stack) {
		if (stack != null) {
			List<String> tags = EtFuturum.getOreStrings(stack);
			if (getEntityItem().getUnlocalizedName().contains("netherite")
					|| tags.contains("oreDebris") || tags.contains("scrapDebris")
					|| tags.contains("ingotNetherite") || tags.contains("blockNetherite")) {
				this.isImmuneToFire = true;
				this.fireResistance = Integer.MAX_VALUE;
			} else if (isImmuneToFire) {
				this.isImmuneToFire = false;
				this.fireResistance = 1;
			}
		}
	}

	@Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
	private void applyFireImmunity(CallbackInfo ci) {
		if (ticksExisted == 1) {
			makeImmuneToFire(getEntityItem());
		}
	}

	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;moveEntity(DDD)V"))
	private void floatLava(EntityItem instance, double mx, double my, double mz) {
		double buoyancy = 0;
		if (isImmuneToFire && this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava) {
			motionY += 0.04D; //Cancels the gravity applied to the item
			my += 0.04D; //Also do this; I want to use the variables for compatibility sake
			buoyancy = 0.3D;
		}
		moveEntity(mx, my + buoyancy, mz);
	}

	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
	private Block noFizzBounce(World instance, int x, int y, int z) { //Returns AIR so the check for lava is false; we do this to remove the fizzing and bouncing
		return isImmuneToFire ? Blocks.air : worldObj.getBlock(x, y, z);
	}

	@Override
	public boolean isBurning() {
		return !isImmuneToFire && super.isBurning();
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "HEAD"), cancellable = true)
	public void attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_, CallbackInfoReturnable<Boolean> cir) {
		if (isImmuneToFire && p_70097_1_.isFireDamage()) {
			cir.setReturnValue(false);
		}
	}
}
