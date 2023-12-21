package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public enum ModsList {
	ENDERLICIOUS("enderlicious"),
	IRON_CHEST("IronChest"),
	NETHERLICIOUS("netherlicious"),
	AETHER_LEGACY("aether_legacy"),
	WAILA("Waila"),
	THAUMCRAFT("Thaumcraft"),
	BLUEPOWER("bluepower"),
	NETHERITEPLUS("netheriteplus"),
	BOTANIA("Botania"),
	HARDCORE_ENDER_EXPANSION("HardcoreEnderExpansion"),
	INDUSTRIAL_CRAFT_2("IC2"),
	SKINPORT("skinport"),
	EARS("ears"),
	BAUBLES("Baubles"),
	BAUBLES_EXPANDED("Baubles|Expanded"),
	MINETWEAKER_3("MineTweaker3"),
	TINKERS_CONSTRUCT("TConstruct"),
	NATURA("Natura"),
	CAMPFIRE_BACKPORT("campfirebackport"),
	BACK_IN_SLIME("bis"),
	MINEFACTORY_RELOADED("MineFactoryReloaded"),
	FOAMFIX("foamfix"),
	MOREPLAYERMODELS("moreplayermodels"),
	DRAGON_BLOCK_C("jinryuudragonblockc"),
	BIOMES_O_PLENTY("BiomesOPlenty"),
	EXTRA_UTILITIES("ExtraUtilities"),
	APPLIED_ENERGISTICS_2("appliedenergistics2"),
	MULTIPART("McMultipart"),
	;

	private final String modID;
	private Boolean isLoaded;
	private String version;

	ModsList(String modID) {
		this.modID = modID;
	}

	public boolean isLoaded() {
		if (isLoaded == null) {
			if (Loader.instance() != null) {
				isLoaded = Loader.isModLoaded(modID);
			} else {
				return false;
			}
		}
		return isLoaded;
	}

	public String getVersion() {
		if (isLoaded() && version == null) {
			version = FMLCommonHandler.instance().findContainerFor(modID).getVersion();
		} else {
			throw new RuntimeException("Cannot get version for mod that is not loaded!");
		}
		return version;
	}
}
