package ganymedes01.etfuturum.compat;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.proxy.CommonProxy;
import net.minecraft.block.Block;

public class CompatAE2 {
	public static final Block createDeepslateCertusQuartzOre(boolean charged) {
		if (EtFuturum.hasAE2) {
			try {
				Class<? super Block> clazz = ReflectionHelper.getClass(CommonProxy.class.getClassLoader(), "appeng.block.solids.OreQuartz" + (charged ? "Charged" : ""));
				return (Block) clazz.getConstructor().newInstance();
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
		return null;
	}
}
