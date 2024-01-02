package ganymedes01.etfuturum.blocks.ores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BlockModdedDeepslateOre extends BaseSubtypesBlock {

	public static final String[] names = new String[]{"deepslate_aluminum_ore", "deepslate_tin_ore", "deepslate_silver_ore", "deepslate_lead_ore", "deepslate_nickel_ore",
			"deepslate_platinum_ore", "deepslate_mythril_ore", "deepslate_uranium_ore", "deepslate_thorium_ore", "deepslate_tungsten_ore", "deepslate_titanium_ore"};
	public static final String[] ores = new String[names.length];

	static {
		for (int i = 0; i < names.length; i++) {
			ores[i] = "ore" + StringUtils.capitalize(names[i].replaceFirst("^deepslate_", "").replace("_ore", ""));
		}
	}

	public BlockModdedDeepslateOre(Material material, String... names) {
		super(material, names);
		setBlockName(Utils.getUnlocalisedName("modded_deepslate_ore"));
		setBlockSound(ModSounds.soundDeepslate);
		setResistance(3);
		setHardness(4.5F);
	}

	public BlockModdedDeepslateOre() {
		this(Material.rock, names);
		setHarvestLevel("pickaxe", 2); //TODO: Config for this since some mods use different harvest levels for these materials
		setHarvestLevel("pickaxe", 1, 0);
		setHarvestLevel("pickaxe", 1, 1);
		setHarvestLevel("pickaxe", 3, 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
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
