package ganymedes01.etfuturum.items;

import net.minecraft.item.ItemStack;

public class ItemNetheriteIngot extends BaseUninflammableItem {

	public ItemNetheriteIngot() {
		super("netherite_ingot");
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack) {
		return true;
	}
}
