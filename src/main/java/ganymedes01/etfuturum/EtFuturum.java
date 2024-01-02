package ganymedes01.etfuturum;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.api.*;
import ganymedes01.etfuturum.api.mappings.BasicMultiBlockSound;
import ganymedes01.etfuturum.blocks.BlockSculk;
import ganymedes01.etfuturum.blocks.BlockSculkCatalyst;
import ganymedes01.etfuturum.client.BuiltInResourcePack;
import ganymedes01.etfuturum.client.DynamicSoundsResourcePack;
import ganymedes01.etfuturum.client.GrayscaleWaterResourcePack;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.command.CommandFill;
import ganymedes01.etfuturum.compat.CompatBaublesExpanded;
import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.compat.CompatThaumcraft;
import ganymedes01.etfuturum.compat.CompatWaila;
import ganymedes01.etfuturum.compat.nei.IMCSenderGTNH;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.proxy.CommonProxy;
import ganymedes01.etfuturum.entities.ModEntityList;
import ganymedes01.etfuturum.items.ItemWoodSign;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.network.*;
import ganymedes01.etfuturum.potion.ModPotions;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.recipes.SmithingTableRecipes;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.world.EtFuturumLateWorldGenerator;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import ganymedes01.etfuturum.world.end.dimension.DimensionProviderEFREnd;
import ganymedes01.etfuturum.world.nether.biome.utils.NetherBiomeManager;
import ganymedes01.etfuturum.world.nether.dimension.DimensionProviderEFRNether;
import ganymedes01.etfuturum.world.structure.OceanMonument;
import makamys.mclib.core.MCLib;
import makamys.mclib.ext.assetdirector.ADConfig;
import makamys.mclib.ext.assetdirector.AssetDirectorAPI;
import net.minecraft.block.*;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@Mod(
		modid = Reference.MOD_ID,
		name = Reference.MOD_NAME,
		version = Reference.VERSION_NUMBER,
		dependencies = Reference.DEPENDENCIES,
		guiFactory = "ganymedes01.etfuturum.configuration.ConfigGuiFactory"
)

public class EtFuturum {

	@Instance("etfuturum")
	public static EtFuturum instance;

	@SidedProxy(clientSide = "ganymedes01.etfuturum.core.proxy.ClientProxy", serverSide = "ganymedes01.etfuturum.core.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper networkWrapper;

	public static CreativeTabs creativeTabItems = new CreativeTabs(Reference.MOD_ID + ".items") {
		@Override
		public Item getTabIconItem() {
			return ConfigBlocksItems.enableNetherite ? ModItems.NETHERITE_SCRAP.get() : ConfigBlocksItems.enablePrismarine ? ModItems.PRISMARINE_SHARD.get() : Items.magma_cream;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void displayAllReleventItems(List p_78018_1_) {
			int j = 1;
			for (byte i = 1; i <= 3; i++) {
				ItemStack firework = new ItemStack(Items.fireworks);
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagCompound nbt2 = new NBTTagCompound();
				nbt2.setByte("Flight", i);
				nbt.setTag("Fireworks", nbt2);
				firework.setTagCompound(nbt);
				p_78018_1_.add(firework);
				j++;
			}
			for (int i : ModEntityList.eggIDs) {
				p_78018_1_.add(new ItemStack(Items.spawn_egg, 1, i));
				j++;
			}
			super.displayAllReleventItems(p_78018_1_);
		}
	};

	public static CreativeTabs creativeTabBlocks = new CreativeTabs(Reference.MOD_ID + ".blocks") {
		@Override
		public Item getTabIconItem() {
			return ConfigBlocksItems.enableSmoker ? Item.getItemFromBlock(ModBlocks.SMOKER.get()) : ConfigBlocksItems.enableChorusFruit ? Item.getItemFromBlock(ModBlocks.CHORUS_FLOWER.get()) : Item.getItemFromBlock(Blocks.ender_chest);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void displayAllReleventItems(List list) {
			list.add(new ItemStack(Blocks.mob_spawner));
			super.displayAllReleventItems(list);

			//Remove the sign items from the list; we'll add them back in a moment
			Iterator<ItemStack> iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemStack stack = iterator.next();
				for (ModItems sign : ModItems.OLD_SIGN_ITEMS) {
					if (stack.getItem() == sign.get()) {
						iterator.remove();
					}
				}
			}

			//Add the sign items back but in a way so they are sorted by their block ID instead of their item ID.
			//This allows them to be in the correct place instead of always at the bottom of the block ID list, since item IDs are always above block IDs
			for (ModItems sign : ModItems.OLD_SIGN_ITEMS) {
				for (ItemStack stack : (List<ItemStack>) list) {
					if (Item.getIdFromItem(stack.getItem()) > Block.getIdFromBlock(((ItemWoodSign) sign.get()).getSignBlock())) {
						list.add(list.indexOf(stack), sign.newItemStack());
						break;
					}
				}
			}
		}
	};

	public static final boolean TESTING = Boolean.parseBoolean(System.getProperty("etfuturum.testing"));
	public static final boolean DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static boolean SNAPSHOT_BUILD = false;
	private static Side effectiveSide;

	static final Map<ItemStack, Integer> DEFAULT_COMPOST_CHANCES = new LinkedHashMap<>();

	@EventHandler
	public void onConstruction(FMLConstructionEvent event) {
		MCLib.init();

		ADConfig config = new ADConfig();

		getSounds(config);

		AssetDirectorAPI.register(config);
	}

	static final String NETHER_FORTRESS = "netherFortress";
	private Field fortressWeightedField;

	public static final boolean hasEnderlicious = Loader.isModLoaded("enderlicious");
	public static final boolean hasIronChest = Loader.isModLoaded("IronChest");
	public static final boolean hasNetherlicious = Loader.isModLoaded("netherlicious");
	public static final boolean hasAetherLegacy = Loader.isModLoaded("aether_legacy");
	public static final boolean hasWaila = Loader.isModLoaded("Waila");
	public static final boolean hasThaumcraft = Loader.isModLoaded("Thaumcraft");
	public static final boolean hasBluePower = Loader.isModLoaded("bluepower");
	public static final boolean hasNP = Loader.isModLoaded("netheriteplus");
	public static final boolean hasBotania = Loader.isModLoaded("Botania");
	public static final boolean hasHEE = Loader.isModLoaded("HardcoreEnderExpansion");
	public static final boolean hasIC2 = Loader.isModLoaded("IC2");
	public static final boolean hasSkinPort = Loader.isModLoaded("skinport");
	public static final boolean hasEars = Loader.isModLoaded("ears");
	public static final boolean hasBaubles = Loader.isModLoaded("Baubles");
	public static final boolean hasBaublesExpanded = Loader.isModLoaded("Baubles|Expanded");
	public static final boolean hasMineTweaker = Loader.isModLoaded("MineTweaker3");
	public static final boolean hasTConstruct = Loader.isModLoaded("TConstruct");
	public static final boolean hasNatura = Loader.isModLoaded("Natura");
	public static final boolean hasCampfireBackport = Loader.isModLoaded("campfirebackport");
	public static final boolean hasBackInSlime = Loader.isModLoaded("bis");
	public static final boolean hasMineFactory = Loader.isModLoaded("MineFactoryReloaded");
	public static final boolean hasFoamFix = Loader.isModLoaded("foamfix");
	public static final boolean hasMorePlayerModels = Loader.isModLoaded("moreplayermodels");
	public static final boolean hasDragonBlockC = Loader.isModLoaded("jinryuudragonblockc");
	public static final boolean hasBiomesOPlenty = Loader.isModLoaded("BiomesOPlenty");
	public static final boolean hasExtraUtils = Loader.isModLoaded("ExtraUtilities");
	public static final boolean hasAE2 = Loader.isModLoaded("appliedenergistics2");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		try {
			Field chestInfo = ChestGenHooks.class.getDeclaredField("chestInfo");
			chestInfo.setAccessible(true);
			if (!((HashMap<String, ChestGenHooks>) chestInfo.get(null)).containsKey(NETHER_FORTRESS)) {
				fortressWeightedField = Class.forName("net.minecraft.world.gen.structure.StructureNetherBridgePieces$Piece").getDeclaredField("field_111019_a");
				fortressWeightedField.setAccessible(true);
				((HashMap<String, ChestGenHooks>) chestInfo.get(null)).put(NETHER_FORTRESS, new ChestGenHooks(NETHER_FORTRESS, (WeightedRandomChestContent[]) fortressWeightedField.get(null), 2, 5));
			}
		} catch (Exception e) {
			System.out.println("Failed to get Nether fortress loot table:");
			e.printStackTrace();
		}

		ModBlocks.init();
		ModItems.init();
		ModEnchantments.init();
		ModPotions.init();
		SpectatorMode.init();

		if (event.getSide() == Side.CLIENT) {

			if (ConfigFunctions.enableNewTextures || ConfigFunctions.enableLangReplacements) {
				BuiltInResourcePack.register("vanilla_overrides");
			}

			GrayscaleWaterResourcePack.inject();

			DynamicSoundsResourcePack.inject();
		}

		if (ConfigWorld.netherDimensionProvider) {
			NetherBiomeManager.init();
		}

		GameRegistry.registerWorldGenerator(EtFuturumWorldGenerator.INSTANCE, 0);
		GameRegistry.registerWorldGenerator(EtFuturumLateWorldGenerator.INSTANCE, Integer.MAX_VALUE);

		OceanMonument.makeMap();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
		networkWrapper.registerMessage(ArmourStandInteractHandler.class, ArmourStandInteractMessage.class, 0, Side.SERVER);
		networkWrapper.registerMessage(BlackHeartParticlesHandler.class, BlackHeartParticlesMessage.class, 1, Side.CLIENT);
		networkWrapper.registerMessage(WoodSignOpenHandler.class, WoodSignOpenMessage.class, 3, Side.CLIENT);
		networkWrapper.registerMessage(BoatMoveHandler.class, BoatMoveMessage.class, 4, Side.SERVER);
		networkWrapper.registerMessage(ChestBoatOpenInventoryHandler.class, ChestBoatOpenInventoryMessage.class, 5, Side.SERVER);
		networkWrapper.registerMessage(StartElytraFlyingHandler.class, StartElytraFlyingMessage.class, 6, Side.SERVER);
		networkWrapper.registerMessage(AttackYawHandler.class, AttackYawMessage.class, 7, Side.CLIENT);

		//Define mod data here instead of in mcmod.info, adapted from Village Names.
		//Thanks AstroTibs!
		//Updated by Makamys to use mcmod.info again but also have color values without glitches.

		event.getModMetadata().autogenerated = false; // stops it from complaining about missing mcmod.info

		event.getModMetadata().name = "\u00a75\u00a7o" + Reference.MOD_NAME; // name 

		String buildVer = Reference.BUILD_VERSION = event.getModMetadata().version;
		SNAPSHOT_BUILD = buildVer.toLowerCase().contains("snapshot") || buildVer.contains("alpha") || buildVer.toLowerCase().contains("beta") || buildVer.toLowerCase().contains("rc");
		event.getModMetadata().version = "\u00a7e" + event.getModMetadata().version; // version (read from mcmod.info)

		event.getModMetadata().credits = Reference.CREDITS; // credits 

		event.getModMetadata().authorList.clear();
		event.getModMetadata().authorList.addAll(Arrays.asList(Reference.AUTHOR_LIST)); // authorList - added as a list

		event.getModMetadata().url = Reference.MOD_URL;

		event.getModMetadata().description = Reference.DESCRIPTION; // description


		event.getModMetadata().logoFile = Reference.LOGO_FILE;

		if (ConfigModCompat.elytraBaublesExpandedCompat > 0 && hasBaublesExpanded) {
			CompatBaublesExpanded.preInit();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (EtFuturum.hasWaila) {
			CompatWaila.register();
		}

		proxy.registerEvents();
		proxy.registerEntities();
		proxy.registerRenderers();
		IMCSenderGTNH.IMCSender();
	}

	@EventHandler
	public void processIMCRequests(IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if (message.key.equals("register-brewing-fuel")) {
				NBTTagCompound nbt = message.getNBTValue();
				ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Fuel"));
				int brews = nbt.getInteger("Brews");
				BrewingFuelRegistry.registerFuel(stack, brews);
			}
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (ConfigFunctions.enableUpdatedFoodValues) {
			((ItemFood) Items.carrot).healAmount = 3;
			((ItemFood) Items.baked_potato).healAmount = 5;
		}

		if (ConfigFunctions.enableUpdatedHarvestLevels) {
			Blocks.packed_ice.setHarvestLevel("pickaxe", 0);
			Blocks.ladder.setHarvestLevel("axe", 0);
			Blocks.melon_block.setHarvestLevel("axe", 0);
		}

		if (ConfigFunctions.enableFloatingTrapDoors) {
			BlockTrapDoor.disableValidation = true;
		}

		if (EtFuturum.hasThaumcraft) {
			CompatThaumcraft.doAspects();
		}

		if (EtFuturum.hasMineTweaker) {
			CompatCraftTweaker.onPostInit();
		}

		Items.blaze_rod.setFull3D();
		Blocks.trapped_chest.setCreativeTab(CreativeTabs.tabRedstone);

		if (ConfigBlocksItems.enableOtherside) {
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(ModItems.OTHERSIDE_RECORD.get(), 0, 1, 1, 1));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(ModItems.OTHERSIDE_RECORD.get(), 0, 1, 1, 1));
		}

		if (ConfigBlocksItems.enablePigstep) {
			ChestGenHooks.addItem(NETHER_FORTRESS, new WeightedRandomChestContent(ModItems.PIGSTEP_RECORD.get(), 0, 1, 1, 5));

			if (fortressWeightedField != null) {
				try {
					Field contents = ChestGenHooks.class.getDeclaredField("contents");
					contents.setAccessible(true);
					ArrayList<WeightedRandomChestContent> fortressContentList;
					fortressContentList = (ArrayList<WeightedRandomChestContent>) contents.get(ChestGenHooks.getInfo("netherFortress"));
					if (!fortressContentList.isEmpty()) {
						WeightedRandomChestContent[] fortressChest = new WeightedRandomChestContent[fortressContentList.size()];
						for (int i = 0; i < fortressContentList.size(); i++) {
							fortressChest[i] = fortressContentList.get(i);
						}
						fortressWeightedField.set(null, fortressChest);
					}
				} catch (Exception e) {
					System.out.println("Failed to fill Nether fortress loot table:");
					e.printStackTrace();
				}
			}
		}

		if (ConfigBlocksItems.enableSmoothStone && EtFuturum.hasBluePower) {
			Item stoneTile = GameRegistry.findItem("bluepower", "stone_tile");
			if (stoneTile != null) {
				Item stoneItem = Item.getItemFromBlock(Blocks.stone);
				FurnaceRecipes.smelting().getSmeltingList().entrySet().removeIf((Predicate<Map.Entry<ItemStack, ItemStack>>) recipe -> recipe.getValue() != null && recipe.getValue().getItem() == stoneTile && recipe.getKey() != null && recipe.getKey().getItem() == stoneItem);
			}
		}

		if (ConfigModCompat.elytraBaublesExpandedCompat > 0 && hasBaublesExpanded) {
			CompatBaublesExpanded.postInit();
		}

		EtFuturumLootTables.init();

		ModRecipes.init();
		DeepslateOreRegistry.init();
		StrippedLogRegistry.init();
		RawOreRegistry.init();
		SmithingTableRecipes.init();
		CompostingRegistry.init();
		BeePlantRegistry.init();
		PistonBehaviorRegistry.init();
	}

	@EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent e) {

		ConfigBase.postInit();

		EtFuturumWorldGenerator.INSTANCE.postInit();

		if (ConfigSounds.newBlockSounds) {
			//Because NP+ uses its own (worse) step sounds for this and it causes the check below that replaces these blocks to fail.
			if (EtFuturum.hasNP) {
				Blocks.nether_brick.setStepSound(ModSounds.soundNetherBricks);
				Blocks.nether_brick_fence.setStepSound(ModSounds.soundNetherBricks);
				Blocks.nether_brick_stairs.setStepSound(ModSounds.soundNetherBricks);
			}
			if (EtFuturum.hasNatura) {
				Block block = GameRegistry.findBlock("Natura", "NetherFurnace");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherLever");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherPressurePlate");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherButton");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
			}
			Blocks.jukebox.setStepSound(Block.soundTypeWood);
			Blocks.noteblock.setStepSound(Block.soundTypeWood);
			Blocks.heavy_weighted_pressure_plate.setStepSound(Block.soundTypeMetal);
			Blocks.light_weighted_pressure_plate.setStepSound(Block.soundTypeMetal);
			Blocks.tripwire_hook.setStepSound(Block.soundTypeWood);
			Blocks.lever.setStepSound(Block.soundTypeStone);
			Blocks.powered_repeater.setStepSound(Block.soundTypeStone);
			Blocks.unpowered_repeater.setStepSound(Block.soundTypeStone);
			Blocks.powered_comparator.setStepSound(Block.soundTypeStone);
			Blocks.unpowered_comparator.setStepSound(Block.soundTypeStone);
			Blocks.sponge.setStepSound(ModSounds.soundSponge);
		}
		if (ConfigBlocksItems.enableDyedBeds) {
			Blocks.bed.blockMaterial = Material.wood;
			Blocks.bed.setStepSound(Block.soundTypeWood);
		}

		//Block registry iterator
		for (Block block : (Iterable<Block>) Block.blockRegistry) {
			if (ConfigFunctions.enableHoeMining) {
				if (block instanceof BlockLeaves || block instanceof BlockHay || block instanceof BlockSponge || block instanceof BlockNetherWart
						|| block instanceof BlockSculk || block instanceof BlockSculkCatalyst) {
					HoeRegistry.addToHoeArray(block);
				}
				HoeRegistry.addToHoeArray(ModBlocks.SHROOMLIGHT.get());
				HoeRegistry.addToHoeArray(ModBlocks.SPONGE.get());
			}

			if (ConfigSounds.newBlockSounds) {
				/*
				 * SOUNDS
				 */
				String blockID = Block.blockRegistry.getNameForObject(block).split(":")[1].toLowerCase();

				SoundType sound = getCustomStepSound(block, blockID);
				if (sound != null) {
					block.setStepSound(sound);
				}

				handleMultiBlockSoundRegistry();
			}
		}

		if (ConfigWorld.netherDimensionProvider && !hasNetherlicious) {
			DimensionProviderEFRNether.init();
		}

		if (TESTING) {
			DimensionProviderEFREnd.init(); // Come back to
		}
	}

	private void handleMultiBlockSoundRegistry() {
		{
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(6, ModSounds.soundNetherBricks);
			mbs.setTypes(14, ModSounds.soundNetherBricks);
			MultiBlockSoundRegistry.multiBlockSounds.put(Blocks.stone_slab, mbs);
			MultiBlockSoundRegistry.multiBlockSounds.put(Blocks.double_stone_slab, mbs);
		}

		if (EtFuturum.hasTConstruct) {
			{
				BasicMultiBlockSound mbs = new BasicMultiBlockSound();
				mbs.setTypes(2, ModSounds.soundNetherrack);
				mbs.setTypes(9, ModSounds.soundBoneBlock);
				MultiBlockSoundRegistry.multiBlockSounds.put(GameRegistry.findBlock("TConstruct", "decoration.multibrick"), mbs);
				MultiBlockSoundRegistry.multiBlockSounds.put(GameRegistry.findBlock("TConstruct", "decoration.multibrickfancy"), mbs);
			}
			{
				BasicMultiBlockSound mbs = new BasicMultiBlockSound() {
					@Override
					public float getPitch(World world, int x, int y, int z, float pitch, MultiBlockSoundRegistry.BlockSoundType type) {
						if (type != MultiBlockSoundRegistry.BlockSoundType.WALK) {
							return pitch * .67F;
						}
						return 1;
					}
				};
				mbs.setTypes(3, ModSounds.soundCopper);
				mbs.setTypes(5, ModSounds.soundCopper);
				MultiBlockSoundRegistry.multiBlockSounds.put(GameRegistry.findBlock("TConstruct", "MetalBlock"), mbs);
			}
		}

		if (ModBlocks.DEEPSLATE_BRICK_WALL.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(1, ModSounds.soundDeepslateTiles);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.DEEPSLATE_BRICK_WALL.get(), mbs);
		}

		if (ModBlocks.DEEPSLATE_BRICKS.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(2, ModSounds.soundDeepslateTiles);
			mbs.setTypes(3, ModSounds.soundDeepslateTiles);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.DEEPSLATE_BRICKS.get(), mbs);
		}

		if (ModBlocks.DEEPSLATE_BRICK_SLAB.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(1, ModSounds.soundDeepslateTiles);
			mbs.setTypes(9, ModSounds.soundDeepslateTiles);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.DEEPSLATE_BRICK_SLAB.get(), mbs);
		}

		if (ModBlocks.AMETHYST_CLUSTER_1.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			for (int i = 0; i < 6; i++) {
				mbs.setTypes(i, ModSounds.soundAmethystBudSmall);
			}
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.AMETHYST_CLUSTER_1.get(), mbs);
		}

		if (ModBlocks.AMETHYST_CLUSTER_2.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			for (int i = 0; i < 6; i++) {
				mbs.setTypes(i, ModSounds.soundAmethystBudLrg);
			}
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.AMETHYST_CLUSTER_2.get(), mbs);
		}

		if (ModBlocks.SPONGE.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(1, ModSounds.soundWetSponge);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.SPONGE.get(), mbs);
		}

		if (ModBlocks.WOOD_PLANKS.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(0, ModSounds.soundNetherWood);
			mbs.setTypes(1, ModSounds.soundNetherWood);
			mbs.setTypes(3, ModSounds.soundCherryWood);
			mbs.setTypes(4, ModSounds.soundBambooWood);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.WOOD_PLANKS.get(), mbs);
		}

		if (ModBlocks.WOOD_FENCE.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(0, ModSounds.soundNetherWood);
			mbs.setTypes(1, ModSounds.soundNetherWood);
			mbs.setTypes(3, ModSounds.soundCherryWood);
			mbs.setTypes(4, ModSounds.soundBambooWood);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.WOOD_FENCE.get(), mbs);
		}

		if (ModBlocks.WOOD_SLAB.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(0, ModSounds.soundNetherWood);
			mbs.setTypes(1, ModSounds.soundNetherWood);
			mbs.setTypes(3, ModSounds.soundNetherWood);
			mbs.setTypes(4, ModSounds.soundBambooWood);

			mbs.setTypes(8, ModSounds.soundNetherWood);
			mbs.setTypes(9, ModSounds.soundNetherWood);
			mbs.setTypes(11, ModSounds.soundCherryWood);
			mbs.setTypes(12, ModSounds.soundBambooWood);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.WOOD_SLAB.get(), mbs);
		}

		if (ModBlocks.DOUBLE_WOOD_SLAB.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(0, ModSounds.soundNetherWood);
			mbs.setTypes(1, ModSounds.soundNetherWood);
			mbs.setTypes(3, ModSounds.soundCherryWood);
			mbs.setTypes(4, ModSounds.soundBambooWood);

			mbs.setTypes(8, ModSounds.soundNetherWood);
			mbs.setTypes(9, ModSounds.soundNetherWood);
			mbs.setTypes(11, ModSounds.soundCherryWood);
			mbs.setTypes(12, ModSounds.soundBambooWood);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.DOUBLE_WOOD_SLAB.get(), mbs);
		}

		if (ModBlocks.PACKED_MUD.isEnabled()) {
			BasicMultiBlockSound mbs = new BasicMultiBlockSound();
			mbs.setTypes(1, ModSounds.soundMudBricks);
			MultiBlockSoundRegistry.multiBlockSounds.put(ModBlocks.PACKED_MUD.get(), mbs);
		}
	}

	/**
	 * As of 2.5.0, I removed some ItemBlocks that are just technical blocks (EG, lit EFR furnaces)
	 * We need to use this event since unregistering specifically an ItemBlock from a block makes Forge mistakenly think a save is corrupted.
	 * I add the EFR name check at the beginning just as a safety precaution.
	 * <p>
	 * Forge does some bad checks on if the item is an ItemBlock before letting you run ignoreItemBlock, leading to erroneous errors.
	 * It doesn't look much different than what I do above but their code rarely spits out "Cannot skip an ItemBlock that doesn't have a Block"
	 * Which makes no sense since if the block != null then we're skipping an ItemBlock that DOES have a block, if my block check != null then what else would it be?
	 * So their check must be wrong. Some of Forge's many registry finders are a little faulty at times.
	 * I already know we're running this on an item, and the only other requirement has a broken check.
	 * So I use reflection to force my way. It's rare for a save to actually throw an error, but just in case....
	 * They really should have just had an ITEMBLOCK mapping type to avoid all these hacky checks.
	 * <p>
	 * All this because Forge falsely declares a world corrupt if you remove an ItemBlock from an existing block.
	 * Gee, all that for removing ItemBlocks.
	 * I wrote this bad code to get around Forge's bad code, only to reveal EVEN MORE bad code in Forge I have to write even worse code to avoid.
	 */

	@EventHandler
	public void onMissingMapping(FMLMissingMappingsEvent e) {
		for (FMLMissingMappingsEvent.MissingMapping mapping : e.getAll()) {
			if (mapping.name.startsWith("etfuturum")) {
				if (Block.getBlockById(mapping.id) != null && mapping.type == GameRegistry.Type.ITEM) {
					mapping.ignore();
					ReflectionHelper.setPrivateValue(FMLMissingMappingsEvent.MissingMapping.class, mapping, FMLMissingMappingsEvent.Action.BLOCKONLY, "action");
				}
			}
		}
	}


	public SoundType getCustomStepSound(Block block, String namespace) {

		if (block.stepSound == Block.soundTypePiston || block.stepSound == Block.soundTypeStone) {

			if (namespace.contains("nether") && namespace.contains("brick")) {
				return ModSounds.soundNetherBricks;
			} else if (namespace.contains("netherrack") || namespace.contains("hellfish")) {
				return ModSounds.soundNetherrack;
			} else if (block == Blocks.quartz_ore || (namespace.contains("nether") && (block instanceof BlockOre || namespace.contains("ore")))) {
				return ModSounds.soundNetherOre;
			} else if (namespace.contains("deepslate")) {
				return namespace.contains("brick") ? ModSounds.soundDeepslateBricks : ModSounds.soundDeepslate;
			} else if (block instanceof BlockNetherWart || (namespace.contains("nether") && namespace.contains("wart"))) {
				return ModSounds.soundCropWarts;
			} else if (namespace.contains("bone") || namespace.contains("ivory")) {
				return ModSounds.soundBoneBlock;
			}
		}

		if (block.stepSound == Block.soundTypeGrass) {
			if (block instanceof BlockVine) {
				return ModSounds.soundVines;
			}

			if (block instanceof BlockLilyPad) {
				return ModSounds.soundWetGrass;
			}
		}

		if (block instanceof BlockCrops || block instanceof BlockStem) {
			return ModSounds.soundCrops;
		}

		if (block.stepSound == Block.soundTypeSand && namespace.contains("soul") && namespace.contains("sand")) {
			return ModSounds.soundSoulSand;
		}

		if (block.stepSound == Block.soundTypeMetal && (namespace.contains("copper") || namespace.contains("tin"))) {
			return ModSounds.soundCopper;
		}

		if (block.getMaterial() == Material.iron && block instanceof BlockHopper) {
			return Block.soundTypeMetal;
		}

		return null;
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		if (ConfigFunctions.enableFillCommand) {
			event.registerServerCommand(new CommandFill());
		}
	}

	public static void copyAttribs(Block to, Block from) {
		to.setHardness(from.blockHardness);
		to.setResistance(from.blockResistance);
		to.setStepSound(from.stepSound);
		to.setLightLevel(from.getLightValue() / 15F);
		to.setLightOpacity(from.getLightOpacity());
		//We do this because Forge methods cannot be Access Transformed
		for (int i = 0; i < 16; i++) {
			to.setHarvestLevel(from.getHarvestTool(i), from.getHarvestLevel(i), i);
		}
	}

	/**
	 * Utility for running string.contains() on a list of strings.
	 */
	public static boolean stringListContainsPhrase(Set<String> set, String string) {
		for (String stringInSet : set) {
			if (string.contains(stringInSet)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> getOreStrings(ItemStack stack) {
		final List<String> list = new ArrayList<>();
		for (int oreID : OreDictionary.getOreIDs(stack)) {
			list.add(OreDictionary.getOreName(oreID));
		}
		return list;
	}

	public static boolean hasDictTag(Block block, String... tags) {
		return hasDictTag(new ItemStack(block), tags);
	}

	public static boolean hasDictTag(Item item, String... tags) {
		return hasDictTag(new ItemStack(item), tags);
	}

	public static boolean hasDictTag(ItemStack stack, String... tags) {
		for (String oreName : getOreStrings(stack)) {
			if (ArrayUtils.contains(tags, oreName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean dictTagsStartWith(Block block, String stringToFind) {
		return dictTagsContain(new ItemStack(block), stringToFind);
	}

	public static boolean dictTagsStartWith(Item item, String stringToFind) {
		return dictTagsContain(new ItemStack(item), stringToFind);
	}

	public static boolean dictTagsStartWith(ItemStack stack, String stringToFind) {
		for (String oreName : getOreStrings(stack)) {
			if (oreName.startsWith(stringToFind)) {
				return true;
			}
		}
		return false;
	}

	public static boolean dictTagsContain(Block block, String stringToFind) {
		return dictTagsContain(new ItemStack(block), stringToFind);
	}

	public static boolean dictTagsContain(Item item, String stringToFind) {
		return dictTagsContain(new ItemStack(item), stringToFind);
	}

	public static boolean dictTagsContain(ItemStack stack, String stringToFind) {
		for (String oreName : getOreStrings(stack)) {
			if (oreName.contains(stringToFind)) {
				return true;
			}
		}
		return false;
	}

	public static PotionEffect getSuspiciousStewEffect(ItemStack stack) {

		if (stack == null)
			return null;

		Item item = stack.getItem();

		if (item == Item.getItemFromBlock(Blocks.red_flower)) {
			switch (stack.getItemDamage()) {
				default:
				case 0:
					return new PotionEffect(Potion.nightVision.id, 100, 0);
				case 1:
					return new PotionEffect(Potion.field_76443_y.id, 7, 0);
				case 2:
					return new PotionEffect(Potion.fireResistance.id, 80, 0);
				case 3:
					return new PotionEffect(Potion.blindness.id, 160, 0);
				case 4:
				case 5:
				case 6:
				case 7:
					return new PotionEffect(Potion.weakness.id, 180, 0);
				case 8:
					return new PotionEffect(Potion.regeneration.id, 160, 0);
			}
		}

		if (item == Item.getItemFromBlock(Blocks.yellow_flower)) {
			return new PotionEffect(Potion.field_76443_y.id, 7, 0);
		}

		if (item == Item.getItemFromBlock(ModBlocks.CORNFLOWER.get())) {
			return new PotionEffect(Potion.jump.id, 120, 0);
		}

		if (item == Item.getItemFromBlock(ModBlocks.LILY_OF_THE_VALLEY.get())) {
			return new PotionEffect(Potion.poison.id, 240, 0);
		}

		if (item == Item.getItemFromBlock(ModBlocks.WITHER_ROSE.get())) {
			return new PotionEffect(Potion.wither.id, 160, 0);
		}
		return null;
	}

	private static void getSounds(ADConfig config) {
		String ver = Reference.MCAssetVer.split("_")[1];
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave14.ogg");
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave15.ogg");
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave16.ogg");
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave17.ogg");
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave18.ogg");
		config.addObject(ver, "minecraft/sounds/ambient/cave/cave19.ogg");

		config.addSoundEvent(ver, "weather.rain", "weather");
		config.addSoundEvent(ver, "weather.rain.above", "weather");

		config.addSoundEvent(ver, "music.nether.nether_wastes", "music");
		config.addSoundEvent(ver, "ambient.nether_wastes.additions", "ambient");
		config.addSoundEvent(ver, "ambient.nether_wastes.loop", "ambient");
		config.addSoundEvent(ver, "ambient.nether_wastes.mood", "ambient");

		config.addSoundEvent(ver, "music.nether.crimson_forest", "music");
		config.addSoundEvent(ver, "ambient.crimson_forest.additions", "ambient");
		config.addSoundEvent(ver, "ambient.crimson_forest.loop", "ambient");
		config.addSoundEvent(ver, "ambient.crimson_forest.mood", "ambient");

		config.addSoundEvent(ver, "music.nether.warped_forest", "music");
		config.addSoundEvent(ver, "ambient.warped_forest.additions", "ambient");
		config.addSoundEvent(ver, "ambient.warped_forest.loop", "ambient");
		config.addSoundEvent(ver, "ambient.warped_forest.mood", "ambient");

		config.addSoundEvent(ver, "music.nether.soul_sand_valley", "music");
		config.addSoundEvent(ver, "ambient.soul_sand_valley.additions", "ambient");
		config.addSoundEvent(ver, "ambient.soul_sand_valley.loop", "ambient");
		config.addSoundEvent(ver, "ambient.soul_sand_valley.mood", "ambient");

		config.addSoundEvent(ver, "music.nether.basalt_deltas", "music");
		config.addSoundEvent(ver, "ambient.basalt_deltas.additions", "ambient");
		config.addSoundEvent(ver, "ambient.basalt_deltas.loop", "ambient");
		config.addSoundEvent(ver, "ambient.basalt_deltas.mood", "ambient");

		config.addSoundEvent(ver, "music_disc.pigstep", "record");
		config.addSoundEvent(ver, "music_disc.otherside", "record");

		config.addSoundEvent(ver, "item.elytra.flying", "player");
		config.addSoundEvent(ver, "enchant.thorns.hit", "player");
		config.addSoundEvent(ver, "entity.boat.paddle_land", "player");
		config.addSoundEvent(ver, "entity.boat.paddle_water", "player");
		config.addSoundEvent(ver, "entity.rabbit.ambient", "neutral");
		config.addSoundEvent(ver, "entity.rabbit.jump", "neutral");
		config.addSoundEvent(ver, "entity.rabbit.attack", "neutral");
		config.addSoundEvent(ver, "entity.rabbit.hurt", "neutral");
		config.addSoundEvent(ver, "entity.rabbit.death", "neutral");
		config.addSoundEvent(ver, "entity.zombie_villager.ambient", "hostile");
		config.addSoundEvent(ver, "entity.zombie_villager.step", "hostile");
		config.addSoundEvent(ver, "entity.zombie_villager.hurt", "hostile");
		config.addSoundEvent(ver, "entity.zombie_villager.death", "hostile");
		config.addSoundEvent(ver, "entity.husk.ambient", "hostile");
		config.addSoundEvent(ver, "entity.husk.step", "hostile");
		config.addSoundEvent(ver, "entity.husk.hurt", "hostile");
		config.addSoundEvent(ver, "entity.husk.death", "hostile");
		config.addSoundEvent(ver, "entity.zombie.converted_to_drowned", "hostile");
		config.addSoundEvent(ver, "entity.husk.converted_to_zombie", "hostile");
		config.addSoundEvent(ver, "entity.stray.ambient", "hostile");
		config.addSoundEvent(ver, "entity.stray.step", "hostile");
		config.addSoundEvent(ver, "entity.stray.hurt", "hostile");
		config.addSoundEvent(ver, "entity.stray.death", "hostile");
		config.addSoundEvent(ver, "entity.skeleton.converted_to_stray", "hostile");
		config.addSoundEvent(ver, "entity.shulker_bullet.hurt", "hostile");
		config.addSoundEvent(ver, "entity.shulker_bullet.hit", "hostile");
		config.addSoundEvent(ver, "entity.shulker.ambient", "hostile");
		config.addSoundEvent(ver, "entity.shulker.open", "hostile");
		config.addSoundEvent(ver, "entity.shulker.close", "hostile");
		config.addSoundEvent(ver, "entity.shulker.shoot", "hostile");
		config.addSoundEvent(ver, "entity.shulker.hurt", "hostile");
		config.addSoundEvent(ver, "entity.shulker.hurt_closed", "hostile");
		config.addSoundEvent(ver, "entity.shulker.death", "hostile");
		config.addSoundEvent(ver, "entity.shulker.teleport", "hostile");
		config.addSoundEvent(ver, "entity.snow_golem.ambient", "neutral");
		config.addSoundEvent(ver, "entity.snow_golem.hurt", "neutral");
		config.addSoundEvent(ver, "entity.snow_golem.death", "neutral");
		config.addSoundEvent(ver, "entity.wither_skeleton.ambient", "hostile");
		config.addSoundEvent(ver, "entity.wither_skeleton.hurt", "hostile");
		config.addSoundEvent(ver, "entity.wither_skeleton.death", "hostile");
		config.addSoundEvent(ver, "entity.wither_skeleton.step", "hostile");
		config.addSoundEvent(ver, "entity.squid.ambient", "neutral");
		config.addSoundEvent(ver, "entity.squid.hurt", "neutral");
		config.addSoundEvent(ver, "entity.squid.death", "neutral");
		config.addSoundEvent(ver, "entity.squid.squirt", "neutral");
		config.addSoundEvent(ver, "entity.witch.ambient", "hostile");
		config.addSoundEvent(ver, "entity.witch.hurt", "hostile");
		config.addSoundEvent(ver, "entity.witch.death", "hostile");
		config.addSoundEvent(ver, "entity.witch.drink", "hostile");
		config.addSoundEvent(ver, "entity.item_frame.add_item", "player");
		config.addSoundEvent(ver, "entity.item_frame.break", "player");
		config.addSoundEvent(ver, "entity.item_frame.place", "player");
		config.addSoundEvent(ver, "entity.item_frame.remove_item", "player");
		config.addSoundEvent(ver, "entity.item_frame.rotate_item", "player");
		config.addSoundEvent(ver, "entity.painting.break", "player");
		config.addSoundEvent(ver, "entity.painting.place", "player");
		config.addSoundEvent(ver, "entity.leash_knot.break", "player");
		config.addSoundEvent(ver, "entity.leash_knot.place", "player");
		config.addSoundEvent(ver, "entity.ender_eye.death", "neutral");
		config.addSoundEvent(ver, "entity.ender_eye.launch", "neutral");
		config.addSoundEvent(ver, "entity.fishing_bobber.retrieve", "neutral");
		config.addSoundEvent(ver, "entity.fishing_bobber.throw", "neutral");
		config.addSoundEvent(ver, "entity.horse.eat", "neutral");
		config.addSoundEvent(ver, "entity.cow.milk", "neutral");
		config.addSoundEvent(ver, "entity.mooshroom.milk", "neutral");
		config.addSoundEvent(ver, "entity.mooshroom.convert", "neutral");
		config.addSoundEvent(ver, "entity.bee.loop", "neutral");
		config.addSoundEvent(ver, "entity.bee.loop_aggressive", "neutral");
		config.addSoundEvent(ver, "entity.bee.hurt", "neutral");
		config.addSoundEvent(ver, "entity.bee.death", "neutral");
		config.addSoundEvent(ver, "entity.bee.pollinate", "neutral");
		config.addSoundEvent(ver, "entity.bee.sting", "neutral");


		config.addSoundEvent(ver, "entity.player.hurt_on_fire", "player");
		config.addSoundEvent(ver, "entity.player.hurt_drown", "player");
		config.addSoundEvent(ver, "entity.player.hurt_sweet_berry_bush", "player");
		config.addSoundEvent(ver, "entity.player.attack.crit", "player");
		config.addSoundEvent(ver, "entity.player.attack.knockback", "player");
		config.addSoundEvent(ver, "entity.player.attack.nodamage", "player");
		config.addSoundEvent(ver, "entity.player.attack.strong", "player");
		config.addSoundEvent(ver, "entity.player.attack.sweep", "player");
		config.addSoundEvent(ver, "entity.player.attack.weak", "player");
		config.addSoundEvent(ver, "entity.player.splash.high_speed", "player");

		config.addSoundEvent(ver, "item.axe.scrape", "player");
		config.addSoundEvent(ver, "item.axe.wax_off", "player");
		config.addSoundEvent(ver, "item.axe.strip", "player");
		config.addSoundEvent(ver, "item.hoe.till", "player");
		config.addSoundEvent(ver, "item.honeycomb.wax_on", "player");
		config.addSoundEvent(ver, "item.totem.use", "player");
		config.addSoundEvent(ver, "item.shovel.flatten", "player");
		config.addSoundEvent(ver, "item.chorus_fruit.teleport", "player");
		config.addSoundEvent(ver, "item.book.page_turn", "player");
		config.addSoundEvent(ver, "item.bucket.fill", "player");
		config.addSoundEvent(ver, "item.bucket.fill_lava", "player");
		config.addSoundEvent(ver, "item.bucket.empty", "player");
		config.addSoundEvent(ver, "item.bucket.empty_lava", "player");
		config.addSoundEvent(ver, "item.bottle.fill", "player");
		config.addSoundEvent(ver, "item.bottle.empty", "player");
		config.addSoundEvent(ver, "item.bone_meal.use", "player");
		config.addSoundEvent(ver, "item.honey_bottle.drink", "player");

		config.addSoundEvent(ver, "item.armor.equip_leather", "player");
		config.addSoundEvent(ver, "item.armor.equip_gold", "player");
		config.addSoundEvent(ver, "item.armor.equip_chain", "player");
		config.addSoundEvent(ver, "item.armor.equip_iron", "player");
		config.addSoundEvent(ver, "item.armor.equip_diamond", "player");
		config.addSoundEvent(ver, "item.armor.equip_netherite", "player");
		config.addSoundEvent(ver, "item.armor.equip_turtle", "player");
		config.addSoundEvent(ver, "item.armor.equip_generic", "player");
		config.addSoundEvent(ver, "item.armor.equip_elytra", "player");

		config.addSoundEvent(ver, "block.note_block.banjo", "record");
		config.addSoundEvent(ver, "block.note_block.bell", "record");
		config.addSoundEvent(ver, "block.note_block.bit", "record");
		config.addSoundEvent(ver, "block.note_block.chime", "record");
		config.addSoundEvent(ver, "block.note_block.cow_bell", "record");
		config.addSoundEvent(ver, "block.note_block.didgeridoo", "record");
		config.addSoundEvent(ver, "block.note_block.flute", "record");
		config.addSoundEvent(ver, "block.note_block.guitar", "record");
		config.addSoundEvent(ver, "block.note_block.harp", "record");
		config.addSoundEvent(ver, "block.note_block.iron_xylophone", "record");
		config.addSoundEvent(ver, "block.note_block.xylophone", "record");

		config.addSoundEvent(ver, "block.barrel.open", "block");
		config.addSoundEvent(ver, "block.barrel.close", "block");
		config.addSoundEvent(ver, "block.chorus_flower.grow", "block");
		config.addSoundEvent(ver, "block.chorus_flower.death", "block");
		config.addSoundEvent(ver, "block.end_portal.spawn", "ambient");
		config.addSoundEvent(ver, "block.end_portal_frame.fill", "block");
		config.addSoundEvent(ver, "block.shulker_box.open", "block");
		config.addSoundEvent(ver, "block.shulker_box.close", "block");
		config.addSoundEvent(ver, "block.sweet_berry_bush.pick_berries", "player");
		config.addSoundEvent(ver, "block.brewing_stand.brew", "block");
		config.addSoundEvent(ver, "block.furnace.fire_crackle", "block");
		config.addSoundEvent(ver, "block.blastfurnace.fire_crackle", "block");
		config.addSoundEvent(ver, "block.smoker.smoke", "block");
		config.addSoundEvent(ver, "block.chest.close", "block");
		config.addSoundEvent(ver, "block.ender_chest.open", "block");
		config.addSoundEvent(ver, "block.ender_chest.close", "block");
		config.addSoundEvent(ver, "block.composter.empty", "block");
		config.addSoundEvent(ver, "block.composter.fill", "block");
		config.addSoundEvent(ver, "block.composter.fill_success", "block");
		config.addSoundEvent(ver, "block.composter.ready", "block");
		config.addSoundEvent(ver, "block.amethyst_block.hit", "block");
		config.addSoundEvent(ver, "block.amethyst_block.chime", "block");
		config.addSoundEvent(ver, "block.smithing_table.use", "player");
		config.addSoundEvent(ver, "block.enchantment_table.use", "player");
		config.addSoundEvent(ver, "block.beacon.activate", "block");
		config.addSoundEvent(ver, "block.beacon.ambient", "block");
		config.addSoundEvent(ver, "block.beacon.deactivate", "block");
		config.addSoundEvent(ver, "block.beacon.power_select", "block");
		config.addSoundEvent(ver, "block.honey_block.slide", "neutral");
		config.addSoundEvent(ver, "block.beehive.drip", "block");
		config.addSoundEvent(ver, "block.beehive.enter", "neutral");
		config.addSoundEvent(ver, "block.beehive.exit", "neutral");
		config.addSoundEvent(ver, "block.beehive.work", "neutral");
		config.addSoundEvent(ver, "block.beehive.shear", "player");
		config.addSoundEvent(ver, "block.sponge.absorb", "block");

		config.addSoundEvent(ver, "block.fence_gate.open", "block");
		config.addSoundEvent(ver, "block.fence_gate.close", "block");
		config.addSoundEvent(ver, "block.nether_wood_fence_gate.open", "block");
		config.addSoundEvent(ver, "block.nether_wood_fence_gate.close", "block");
		config.addSoundEvent(ver, "block.cherry_wood_fence_gate.open", "block");
		config.addSoundEvent(ver, "block.cherry_wood_fence_gate.close", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_fence_gate.open", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_fence_gate.close", "block");

		config.addSoundEvent(ver, "block.wooden_door.open", "block");
		config.addSoundEvent(ver, "block.wooden_door.close", "block");
		config.addSoundEvent(ver, "block.nether_wood_door.open", "block");
		config.addSoundEvent(ver, "block.nether_wood_door.close", "block");
		config.addSoundEvent(ver, "block.cherry_wood_door.open", "block");
		config.addSoundEvent(ver, "block.cherry_wood_door.close", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_door.open", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_door.close", "block");
		config.addSoundEvent(ver, "block.iron_door.open", "block");
		config.addSoundEvent(ver, "block.iron_door.close", "block");

		config.addSoundEvent(ver, "block.wooden_trapdoor.open", "block");
		config.addSoundEvent(ver, "block.wooden_trapdoor.close", "block");
		config.addSoundEvent(ver, "block.nether_wood_trapdoor.open", "block");
		config.addSoundEvent(ver, "block.nether_wood_trapdoor.close", "block");
		config.addSoundEvent(ver, "block.cherry_wood_trapdoor.open", "block");
		config.addSoundEvent(ver, "block.cherry_wood_trapdoor.close", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_trapdoor.open", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_trapdoor.close", "block");
		config.addSoundEvent(ver, "block.iron_trapdoor.open", "block");
		config.addSoundEvent(ver, "block.iron_trapdoor.close", "block");

		config.addSoundEvent(ver, "block.wooden_button.click_off", "block");
		config.addSoundEvent(ver, "block.wooden_button.click_on", "block");
		config.addSoundEvent(ver, "block.nether_wood_button.click_off", "block");
		config.addSoundEvent(ver, "block.nether_wood_button.click_on", "block");
		config.addSoundEvent(ver, "block.cherry_wood_button.click_off", "block");
		config.addSoundEvent(ver, "block.cherry_wood_button.click_on", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_button.click_off", "block");
		config.addSoundEvent(ver, "block.bamboo_wood_button.click_on", "block");

		config.addSoundEvent(ver, "block.wooden_pressure_plate.click_off", "block");
		config.addSoundEvent(ver, "block.wooden_pressure_plate.click_on", "block");
		config.addSoundEvent(ver, "block.nether_wood_pressure_plate.click_off", "block");
		config.addSoundEvent(ver, "block.nether_wood_pressure_plate.click_on", "block");
		config.addSoundEvent(ver, "block.cherry_wood_pressure_plate.click_off", "block");
		config.addSoundEvent(ver, "block.cherry_wood_pressure_plate.click_on", "block");
		config.addSoundEvent(ver, "block.metal_pressure_plate.click_off", "block");
		config.addSoundEvent(ver, "block.metal_pressure_plate.click_on", "block");

		//Automatically register block sounds for AssetDirector, but only if they contain the MC version (which means it needs to be registered here)
		//Then we remove the mc version prefix and register that sound.

		for (ModSounds.CustomSound sound : ModSounds.getSounds()) {
			if (sound.getStepResourcePath().startsWith(Reference.MCAssetVer)) { //Step sound
				config.addSoundEvent(ver, sound.getStepResourcePath().substring(Reference.MCAssetVer.length() + 1), "neutral");
			}
			if (sound.func_150496_b().startsWith(Reference.MCAssetVer)) { //Place sound
				config.addSoundEvent(ver, sound.func_150496_b().substring(Reference.MCAssetVer.length() + 1), "block");
			}
			if (sound.getBreakSound().startsWith(Reference.MCAssetVer)) { //Break sound
				config.addSoundEvent(ver, sound.getBreakSound().substring(Reference.MCAssetVer.length() + 1), "block");
			}
		}
	}
}
