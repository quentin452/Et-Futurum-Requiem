package ganymedes01.etfuturum.spectator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.core.utils.helpers.SafeEnumHelperClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class SpectatorMode {
	public static final SpectatorMode INSTANCE = new SpectatorMode();
	public static final IEntitySelector EXCEPT_SPECTATING;
	protected static final Map<EntityPlayer, Entity> SPECTATING_ENTITIES = new WeakHashMap<>();

	//TODO: DO NOT MAKE THIS A LAMBDA! INTELLIJ SUGGESTS IT BUT FOR SOME REASON IT CAUSES AN INITIALIZER EXCEPTION SPECIFICALLY IN THE LIVE GAME AND NOT IN DEV!
	//Made that a TODO so the text would be extra bright and hard to miss.
	static {
		EXCEPT_SPECTATING = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity p_82704_1_) {
				return !(p_82704_1_ instanceof EntityPlayer) || !isSpectator((EntityPlayer) p_82704_1_);
			}
		};
	}


	protected SpectatorMode() {
	}

	public static WorldSettings.GameType SPECTATOR_GAMETYPE = null;

	public static void init() {
		if (ConfigMixins.enableSpectatorMode)
			SPECTATOR_GAMETYPE = SafeEnumHelperClient.addGameType("spectator", 3, "Spectator");
	}

	public static boolean isSpectator(EntityPlayer player) {
		if (player == null || player instanceof FakePlayer || player.worldObj == null)
			return false;
		if (player.worldObj.isRemote) {
			if (player == FMLClientHandler.instance().getClient().thePlayer && FMLClientHandler.instance().getClient().playerController != null)
				return FMLClientHandler.instance().getClient().playerController.currentGameType == SPECTATOR_GAMETYPE;
			return false;
		}
		return player instanceof EntityPlayerMP && ((EntityPlayerMP) player).theItemInWorldManager.getGameType() == SPECTATOR_GAMETYPE;
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		if (isSpectator(event.entityPlayer)) {
			if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
				event.setCanceled(true);
			} else {
				if (!event.world.blockExists(event.x, event.y, event.z)) {
					event.setCanceled(true); //I don't trust other mods not to do weird shit in this scenario
					return;
				}
				TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
				if (!canSpectatorSelect(te)) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onInteract(BlockEvent.PlaceEvent event) {
		if (isSpectator(event.player)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		if (isSpectator(event.entityPlayer)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onInteract(AttackEntityEvent event) {
		if (isSpectator(event.entityPlayer)) {
			if (!SPECTATING_ENTITIES.containsKey(event.entityPlayer)) {
				SPECTATING_ENTITIES.put(event.entityPlayer, event.target);
				if (event.entityPlayer.worldObj.isRemote && event.entityPlayer instanceof EntityClientPlayerMP) {
					Minecraft.getMinecraft().ingameGUI.func_110326_a(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())), false);
				}
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			boolean isSpec = isSpectator(event.player);
			if (!isSpec && event.player.noClip) {
				event.player.setInvisible(false);
				SPECTATING_ENTITIES.remove(event.player);
			}
			event.player.noClip = isSpec;
			/* reuse value for spectator check */
			if (isSpec) {
				event.player.onGround = false;
				event.player.setInvisible(true);
			}

			Entity entityToSpectate = SPECTATING_ENTITIES.get(event.player);
			if (entityToSpectate != null) {
				if (entityToSpectate.isDead || event.player.isSneaking()) {
					SPECTATING_ENTITIES.remove(event.player);
					return;
				}
				followEntity(event.player, entityToSpectate);
			}
		}
	}

	protected void followEntity(EntityPlayer player, Entity entity) {
		float pitch;
		float yaw;
		if (entity instanceof EntityLiving) {
			pitch = ((EntityLiving) entity).cameraPitch;
			yaw = ((EntityLiving) entity).rotationYawHead;
		} else {
			pitch = entity.rotationPitch;
			yaw = entity.rotationYaw;
		}
		player.setLocationAndAngles(entity.posX, (entity.posY - player.yOffset) + entity.getEyeHeight(), entity.posZ, yaw, pitch);
		player.motionX = entity.motionX;
		player.motionY = entity.motionY;
		player.motionZ = entity.motionZ;
		if (player.isSprinting()) {
			player.setSprinting(false);
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		if (isSpectator(event.entityPlayer)) {
			if (event.entityPlayer.worldObj.isRemote) {
				EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
				if (event.entityPlayer == player && FMLClientHandler.instance().getClient().playerController != null) {
					FMLClientHandler.instance().getClient().playerController.stepSoundTickCounter = -5;
					FMLClientHandler.instance().getClient().playerController.isHittingBlock = false;
				}
			}
			event.newSpeed = 0;
		}
	}


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void itemToss(ItemTossEvent event) {
		if (isSpectator(event.player)) {
			event.setCanceled(true);
			ItemStack item = event.entityItem.getEntityItem();
			event.player.inventory.addItemStackToInventory(item);
		}
	}

	public static boolean canSpectatorSelect(TileEntity te) {
		return te instanceof IInventory || te instanceof TileEntityEnderChest;
	}
}
