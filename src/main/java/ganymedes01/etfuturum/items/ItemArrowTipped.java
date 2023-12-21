package ganymedes01.etfuturum.items;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.dispenser.DispenserBehaviourTippedArrow;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.*;
import java.util.Map.Entry;

public class ItemArrowTipped extends Item {
	private static final Map field_77835_b = new LinkedHashMap();

	@SideOnly(Side.CLIENT)
	private IIcon tipIcon;

	private final HashMap effectCache = new HashMap();

	public ItemArrowTipped() {
		setHasSubtypes(true);
		setTextureName("tipped_arrow");
		setUnlocalizedName(Utils.getUnlocalisedName("tipped_arrow"));
		setCreativeTab(EtFuturum.creativeTabItems);

		if (ConfigBlocksItems.enableTippedArrows)
			BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviourTippedArrow());
	}

	private List<PotionEffect> getPotionEffects(int meta, boolean flag) {
		List<PotionEffect> list = PotionHelper.getPotionEffects(meta, flag);
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				PotionEffect effect = list.get(i);
				list.set(i, new PotionEffect(effect.getPotionID(), effect.getDuration() / 8, effect.getAmplifier()));
			}
		}
		return list;
	}

	public List getEffects(ItemStack stack) {
		if (stack.hasTagCompound()) {
			if (stack.getTagCompound().hasKey("Potion", 10)) {
				NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("Potion");
				PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(nbt);
//              for(int meta : (Set<Integer>)effectCache.keySet()) {
//                  List effects = getPotionEffects(meta, false);
//                  if(effects != null && effect.equals(effects.get(0))) {
//                      stack.setItemDamage(meta);
//                      stack.setTagCompound(null);
//                  }
//              }
//              if(stack.hasTagCompound()) {
				List list = new ArrayList();
				list.add(effect);
				return list;
//              }
			} else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
				ArrayList arraylist = new ArrayList();
				NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
					PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

					if (potioneffect != null) {
						arraylist.add(potioneffect);
					}
				}

				return arraylist;
			}
		}
		List list = (List) this.effectCache.get(stack.getItemDamage());
		if (list == null) {
			list = getPotionEffects(stack.getItemDamage(), false);
			this.effectCache.put(stack.getItemDamage(), list);
		}
		return list;
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		int j;

		if (field_77835_b.isEmpty()) {
			for (int i = 1; i <= 15; ++i) {
				int k;

				k = i | 8192;

				for (int l = 0; l <= 2; ++l) {
					int i1 = k;

					if (l != 0) {
						if (l == 1) {
							i1 = k | 32;
						} else if (l == 2) {
							i1 = k | 64;
						}
					}
					List list1 = getPotionEffects(i1, false);

					if (list1 != null && !list1.isEmpty()) {
						field_77835_b.put(list1, i1);
					}
				}
			}
		}

		Iterator iterator = field_77835_b.values().iterator();

		while (iterator.hasNext()) {
			j = (Integer) iterator.next();
			list.add(new ItemStack(item, 1, j));
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		itemIcon = reg.registerIcon(getIconString() + "_base");
		tipIcon = reg.registerIcon(getIconString() + "_head");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (pass == 0 && stack.getItemDamage() == 0 && stack.hasTagCompound() && stack.getTagCompound().hasKey("Potion", 10)) {
			NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("Potion");
			PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(nbt);
			if (effect != null && effect.getPotionID() >= 0 && effect.getPotionID() < Potion.potionTypes.length) {
				return Potion.potionTypes[effect.getPotionID()].getLiquidColor();
			}
		}
		return Items.potionitem.getColorFromItemStack(stack, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? tipIcon : super.getIcon(stack, pass);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		String s = "item.etfuturum.tipped_arrow";

		List list = getEffects(stack);
		String s1;

		if (list != null && !list.isEmpty()) {
			if (stack.getItemDamage() > 0 || (stack.hasTagCompound() && stack.getTagCompound().hasKey("Potion", 10))) {
				s1 = ((PotionEffect) list.get(0)).getEffectName();
				return s + "." + s1;
			}
		} else if (stack.getItemDamage() == 0) {
			return "item.etfuturum.tipped_arrow.effect.empty";
		}

		return s;


//      PotionEffect effect = getEffect(stack);
//      if (effect == null || effect.getPotionID() < 0 || effect.getPotionID() >= Potion.potionTypes.length)
//          //return super.getUnlocalizedName(stack); 
//          return "tipped_arrow.effect.empty";
//
//      Potion potion = Potion.potionTypes[effect.getPotionID()];
//      return "tipped_arrow." + potion.getName();
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
		List list1 = getEffects(stack);
		HashMultimap hashmultimap = HashMultimap.create();
		Iterator iterator1;

		if (list1 != null && !list1.isEmpty()) {
			iterator1 = list1.iterator();

			while (iterator1.hasNext()) {
				PotionEffect potioneffect = (PotionEffect) iterator1.next();
				String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				Map map = potion.func_111186_k();

				if (map != null && map.size() > 0) {
					Iterator iterator = map.entrySet().iterator();

					while (iterator.hasNext()) {
						Entry entry = (Entry) iterator.next();
						AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						hashmultimap.put(((IAttribute) entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
					}
				}

				if (potioneffect.getAmplifier() > 0) {
					s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if (potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
				}

				if (potion.isBadEffect()) {
					list.add(EnumChatFormatting.RED + s1);
				} else {
					list.add(EnumChatFormatting.GRAY + s1);
				}
			}
		} else {
			String s = StatCollector.translateToLocal("potion.empty").trim();
			list.add(EnumChatFormatting.GRAY + s);
		}

		if (!hashmultimap.isEmpty()) {
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
			iterator1 = hashmultimap.entries().iterator();

			while (iterator1.hasNext()) {
				Entry entry1 = (Entry) iterator1.next();
				AttributeModifier attributemodifier2 = (AttributeModifier) entry1.getValue();
				double d0 = attributemodifier2.getAmount();
				double d1;

				if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}

				if (d0 > 0.0D) {
					list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				} else if (d0 < 0.0D) {
					d1 *= -1.0D;
					list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				}
			}
		}
	}

}