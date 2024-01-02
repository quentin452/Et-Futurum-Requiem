package ganymedes01.etfuturum.world.nether.biome;

import cpw.mods.fml.common.registry.EntityRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.world.nether.biome.decorator.NetherForestDecorator;
import ganymedes01.etfuturum.world.nether.biome.utils.IBiomeColor;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeWarpedForest extends NetherBiomeBase {

	public BiomeWarpedForest(int id) {
		super(id, NetherForestDecorator.newWarpedForestDecorator());
		this.setColor(0x49907B);
		fogSkyColor = 0x5700AF;
		this.topBlock = ModBlocks.NYLIUM.get();
		this.field_150604_aj = 1;
		this.fillerBlock = Blocks.netherrack;

		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));
	}

	public float getSpawningChance() {
		return 0.035F;
	}
}