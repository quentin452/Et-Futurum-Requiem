package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Modded raw ore class that doesn't add the raw ores to the Creative menu if there would be no ore for them to base off of.
 */
public class ItemModdedRawOre extends BaseSubtypesItem {

	public static final String[] names = new String[]{"raw_aluminum", "raw_tin", "raw_silver", "raw_lead", "raw_nickel", "raw_platinum", "raw_mythril",
			"raw_uranium", "raw_thorium", "raw_tungsten", "raw_titanium", "raw_zinc", "raw_magnesium", "raw_boron"};
	public static final String[] ores = new String[names.length];

	static {
		for (int i = 0; i < names.length; i++) {
			ores[i] = "ingot" + StringUtils.capitalize(names[i].replaceFirst("^raw_", ""));
		}
	}

	public ItemModdedRawOre() {
		super(names);
		setNames("modded_raw_ore");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for (int i = 0; i < ores.length; i++) {
			ItemStack stack = new ItemStack(item, 1, i);
			if (!EtFuturum.getOreStrings(stack).isEmpty()) {
				list.add(stack);
			}
		}
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}
}
