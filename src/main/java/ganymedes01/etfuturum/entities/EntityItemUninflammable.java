package ganymedes01.etfuturum.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

@Deprecated
/**
 * No longer used in favor of a mixin.
 */
public class EntityItemUninflammable extends EntityItem {

	public EntityItemUninflammable(World p_i1711_1_) {
		super(p_i1711_1_);
		this.isImmuneToFire = true;
		this.fireResistance = Integer.MAX_VALUE;
	}

	public EntityItemUninflammable(World p_149642_1_, double d, double e, double f, ItemStack p_149642_5_) {
		super(p_149642_1_, d, e, f, p_149642_5_);
		this.isImmuneToFire = true;
		this.fireResistance = Integer.MAX_VALUE;
	}

	@Override
	protected void dealFireDamage(int p_70081_1_) {
		//ignore
	}

	@Override
	public void onUpdate() {
		EntityItem item = new EntityItem(worldObj, posX, posY, posZ, getEntityItem());
		NBTTagCompound comp = new NBTTagCompound();
		writeToNBT(comp);
		item.readEntityFromNBT(comp);
		worldObj.onEntityAdded(item);
		setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		if (p_70097_1_ == DamageSource.inFire || p_70097_1_ == DamageSource.onFire || p_70097_1_ == DamageSource.lava)
			return false;

		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

}
