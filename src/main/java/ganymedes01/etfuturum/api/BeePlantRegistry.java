package ganymedes01.etfuturum.api;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.blocks.BlockBerryBush;
import ganymedes01.etfuturum.blocks.BlockChorusFlower;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class BeePlantRegistry {

	private static final List<RegistryMapping<Block>> BEE_FLOWERS = Lists.newArrayList();
	private static final List<Block> BEE_CROPS = Lists.newArrayList();

	/**
	 * When adding a BlockDoublePlant, note that any meta above 7 will be rejected. Bees are supposed to go to the top half of the flower, but the bottom metas are unique.
	 * Provide the bottom meta; when bees search for a flower in the world they'll go to the top half of your BlockDoublePlant. No need to add the top half metas manually.
	 *
	 * @param block
	 */
	public static void addFlower(Block block, int meta) {
		if (block instanceof BlockDoublePlant && meta > 7 && meta != OreDictionary.WILDCARD_VALUE) {
			throw new IllegalArgumentException("BlockDoublePlant can't have meta greater than 7, 8 and above are used by the top half. Bees will go to the top half if the bottom meta is valid.");
		}
		if (ModRecipes.validateItems(block)) {
			BEE_FLOWERS.add(new RegistryMapping<>(block, meta));
		}
	}

	public static void addCrop(Block block) {
		if (!(block instanceof IGrowable)) {
			throw new IllegalArgumentException("Bee crops must be instance of IGrowable!");
		}
		BEE_CROPS.add(block);
	}

	public static void removeFlower(Block block, int meta) {
		BEE_FLOWERS.remove(new RegistryMapping<>(block, meta));
	}

	public static void removeCrop(Block block) {
		BEE_CROPS.remove(block);
	}

	public static boolean isFlower(Block block, int meta) {
		return BEE_FLOWERS.contains(new RegistryMapping<>(block, meta));
	}

	public static boolean isCrop(Block block) {
		return BEE_CROPS.contains(block);
	}

	public static void init() {
		if (ConfigEntities.enableBees) {
			for (Block block : (Iterable<Block>) Block.blockRegistry) {
				if (block instanceof BlockFlower || block instanceof BlockChorusFlower) {
					addFlower(block, OreDictionary.WILDCARD_VALUE);
				}
				if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockBerryBush) {
					addCrop(block);
					//TODO: Add cave vines as a pollinatable crop, when they get added
				}
			}
			addFlower(Blocks.double_plant, 0);
			addFlower(Blocks.double_plant, 1);
			addFlower(Blocks.double_plant, 4);
			addFlower(Blocks.double_plant, 5);

			addFlower(ModBlocks.AZALEA.get(), 1);
			//TODO: This should have flowering azalea leaves, cherry leaves, pink petals, mangrove propagules, and spore blossoms as flowers, when added
		}
	}
}
