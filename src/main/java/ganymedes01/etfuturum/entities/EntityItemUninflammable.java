package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

import java.util.Iterator;

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
		if (isBurning())
			extinguish();

		ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
		if (stack != null && stack.getItem() != null) {
			if (stack.getItem().onEntityItemUpdate(this)) {
				return;
			}
		}

		if (this.getEntityItem() == null) {
			this.setDead();
		} else if (ConfigFunctions.enableNetheriteFlammable) {
			EntityItem item = new EntityItem(this.worldObj);
			item.copyDataFrom(this, true);
			item.delayBeforeCanPickup = this.delayBeforeCanPickup;
			item.copyLocationAndAnglesFrom(this);
			item.worldObj.spawnEntityInWorld(item);
			this.setDead();
		} else {
			onEntityUpdate();

			if (this.delayBeforeCanPickup > 0) {
				--this.delayBeforeCanPickup;
			}

			boolean inlava = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
					.getMaterial() == Material.lava;
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
			if (inlava) {
				this.moveEntity(this.motionX, this.motionY + 0.036D, this.motionZ);
			} else {
				this.motionY -= 0.03999999910593033D;
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
			}
			boolean flag = (int) this.prevPosX != (int) this.posX || (int) this.prevPosY != (int) this.posY || (int) this.prevPosZ != (int) this.posZ;

			if (flag || this.ticksExisted % 25 == 0) {
				if (!this.worldObj.isRemote) {
					this.searchForOtherItemsNearby();
				}
			}

			float f = 0.98F;

			if (this.onGround) {
				f = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
			}

			this.motionX *= f;
			this.motionY *= 0.9800000190734863D;
			this.motionZ *= f;

			if (this.onGround) {
				this.motionY *= -0.5D;
			}

			++this.age;

			ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

			if (!this.worldObj.isRemote && this.age >= lifespan) {
				if (item != null) {
					ItemExpireEvent event = new ItemExpireEvent(this, (item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, worldObj)));
					if (MinecraftForge.EVENT_BUS.post(event)) {
						lifespan += event.extraLife;
					} else {
						this.setDead();
					}
				} else {
					this.setDead();
				}
			}

			if (item != null && item.stackSize <= 0) {
				this.setDead();
			}
		}
	}

	private void searchForOtherItemsNearby() {
		Iterator<EntityItem> iterator = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D)).iterator();

		while (iterator.hasNext()) {
			EntityItem entityitem = iterator.next();
			this.combineItems(entityitem);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		if (p_70097_1_ == DamageSource.inFire || p_70097_1_ == DamageSource.onFire || p_70097_1_ == DamageSource.lava)
			return false;

		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

}
