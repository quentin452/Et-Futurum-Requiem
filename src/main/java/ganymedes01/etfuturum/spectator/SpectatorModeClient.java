package ganymedes01.etfuturum.spectator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;

public class SpectatorModeClient extends SpectatorMode {
	public static final SpectatorModeClient INSTANCE = new SpectatorModeClient();
	private boolean doRefreshModel = false;
	private boolean canSelect = false;

	@SideOnly(Side.CLIENT)
	private static void setBipedVisible(ModelBiped biped, boolean visible) {
		biped.bipedHead.showModel = visible;
		biped.bipedHeadwear.showModel = visible;
		biped.bipedBody.showModel = visible;
		biped.bipedRightArm.showModel = visible;
		biped.bipedLeftArm.showModel = visible;
		biped.bipedRightLeg.showModel = visible;
		biped.bipedLeftLeg.showModel = visible;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		if (!SPECTATING_ENTITIES.containsKey(event.entityPlayer)) {
			if (isSpectator(event.entityPlayer)) {
				setBipedVisible(event.renderer.modelBipedMain, false);
				event.renderer.modelBipedMain.bipedHead.showModel = true;
				event.renderer.modelBipedMain.bipedHeadwear.showModel = true;
			} else {
				setBipedVisible(event.renderer.modelBipedMain, true);
			}
		} else {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderPlayerArmor(RenderPlayerEvent.Specials.Pre event) {
		if (isSpectator(event.entityPlayer)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderEntity(RenderLivingEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		Entity entity2 = SPECTATING_ENTITIES.get(player);
		if (isSpectator(player) && entity2 != null && entity2.equals(event.entity) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			event.setCanceled(true);
		}
	}

	private static boolean hadHeldItemTooltips;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR || (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && !canSelect)) {
				event.setCanceled(true);
			}
			if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
				hadHeldItemTooltips = Minecraft.getMinecraft().gameSettings.heldItemTooltips;
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = false;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public void onOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = hadHeldItemTooltips;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onHandRender(RenderHandEvent event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			event.setCanceled(true);
			doRefreshModel = true;
		} else if (doRefreshModel) {
//          Redraws the player model for one frame off-screen so it refreshes. Also make sure we only run this logic if this code is targeting the player we're playing as.
//          This is because in some cases loading the player model in 3rd person or the inventory and then going back to another game mode makes the hand invisible.
			doRefreshModel = false;
			RenderManager.instance.renderEntityWithPosYaw(Minecraft.getMinecraft().thePlayer, -180.0D, -180.0D, -180.0D, 0.0F, 0.0F);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onFireRender(RenderBlockOverlayEvent event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			event.setCanceled(true);
		}
	}

	/* TODO look into increasing the distance instead of outright disabling it */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
		if (event.entity instanceof EntityPlayer) {
			if (isSpectator((EntityPlayer) event.entity)) {
				if (event.block.getMaterial().isLiquid()) {
					event.setCanceled(true);
					event.density = 0;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		if (isSpectator(event.player)) {
			canSelect = SpectatorMode.canSpectatorSelect(Minecraft.getMinecraft().theWorld.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ)) || (event.target.entityHit != null && !SPECTATING_ENTITIES.containsKey(event.player));
			if (!canSelect) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityClientPlayerMP player = FMLClientHandler.instance().getClientPlayerEntity();
		if (isSpectator(player)) {
			if (!player.capabilities.isFlying) {
				player.capabilities.isFlying = true;
				player.sendPlayerAbilities();
			}

			Entity entityToSpectate = SPECTATING_ENTITIES.get(player);
			if (entityToSpectate != null) {
				if (entityToSpectate.isDead || player.isSneaking()) {
					SPECTATING_ENTITIES.remove(player);
					return;
				}
				followEntity(player, entityToSpectate);
			}
		}
	}
}
