package ganymedes01.etfuturum.world;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EtFuturumWorldListener implements IWorldAccess {

	private final World world;
	private static final Map<Block, Block> replacements = new HashMap<>();

	public EtFuturumWorldListener(World theWorld) {
		world = theWorld;
		if (ModBlocks.BREWING_STAND.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.brewing_stand, ModBlocks.BREWING_STAND.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.BREWING_STAND.get(), Blocks.brewing_stand);
			}
		}


		if (ModBlocks.BEACON.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.beacon, ModBlocks.BEACON.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.BEACON.get(), Blocks.beacon);
			}
		}

		if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.enchanting_table, ModBlocks.ENCHANTMENT_TABLE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.ENCHANTMENT_TABLE.get(), Blocks.enchanting_table);
			}
		}

		if (ModBlocks.ANVIL.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.anvil, ModBlocks.ANVIL.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.ANVIL.get(), Blocks.anvil);
			}
		}

		if (ModBlocks.SPONGE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.sponge, ModBlocks.SPONGE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.SPONGE.get(), Blocks.sponge);
			}
		}

		if (ModBlocks.DAYLIGHT_DETECTOR.isEnabled()) {
			if (ConfigWorld.tileReplacementMode != -1) {
				replacements.put(ModBlocks.DAYLIGHT_DETECTOR.get(), Blocks.daylight_detector);
			}
		}

		if (ModsList.BACK_IN_SLIME.isLoaded() && ConfigMixins.betterPistons) {
			replacements.put(GameRegistry.findBlock("bis", "SlimePistonBase"), Blocks.piston);
			replacements.put(GameRegistry.findBlock("bis", "StickySlimePistonBase"), Blocks.sticky_piston);
			replacements.put(GameRegistry.findBlock("bis", "SlimePistonHead"), Blocks.piston_head);
			if (ModBlocks.SLIME.isEnabled()) {
				replacements.put(GameRegistry.findBlock("bis", "SlimeBlock"), ModBlocks.SLIME.get());
			}
		}
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {

		if (replacements.isEmpty() || !world.blockExists(x, y, z))
			return;

		Block replacement;
		TileEntity tile;

		if ((replacement = replacements.get(world.getBlock(x, y, z))) == null)
			return;

		tile = world.getTileEntity(x, y, z);
		NBTTagCompound nbt = new NBTTagCompound();
		if (tile != null) {
			tile.writeToNBT(nbt);
			if (tile instanceof IInventory) {
				IInventory invt = (IInventory) tile;
				for (int j = 0; j < invt.getSizeInventory(); j++) {
					invt.setInventorySlotContents(j, null);
				}
			}
		}

		world.setBlock(x, y, z, replacement, world.getBlockMetadata(x, y, z), 2);
		TileEntity newTile = world.getTileEntity(x, y, z);
		if (newTile != null) {
			newTile.readFromNBT(nbt);
		}

	}

	@Override
	public void markBlockForRenderUpdate(int p_147588_1_, int p_147588_2_, int p_147588_3_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_,
											  int p_147585_5_, int p_147585_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSound(String p_72704_1_, double p_72704_2_, double p_72704_4_, double p_72704_6_, float p_72704_8_,
						  float p_72704_9_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSoundToNearExcept(EntityPlayer p_85102_1_, String p_85102_2_, double p_85102_3_, double p_85102_5_,
									  double p_85102_7_, float p_85102_9_, float p_85102_10_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnParticle(String p_72708_1_, double p_72708_2_, double p_72708_4_, double p_72708_6_,
							  double p_72708_8_, double p_72708_10_, double p_72708_12_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityCreate(Entity p_72703_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityDestroy(Entity p_72709_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playRecord(String p_72702_1_, int p_72702_2_, int p_72702_3_, int p_72702_4_) {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_, int p_82746_4_, int p_82746_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_, int p_72706_3_, int p_72706_4_, int p_72706_5_,
						   int p_72706_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyBlockPartially(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_,
									  int p_147587_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStaticEntitiesChanged() {
		// TODO Auto-generated method stub

	}

}
