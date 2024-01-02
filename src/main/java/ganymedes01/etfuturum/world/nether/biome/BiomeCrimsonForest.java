package ganymedes01.etfuturum.world.nether.biome;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.world.nether.biome.decorator.NetherForestDecorator;
import ganymedes01.etfuturum.world.nether.biome.utils.IBiomeColor;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeCrimsonForest extends NetherBiomeBase {

	public BiomeCrimsonForest(int id) {
		super(id, NetherForestDecorator.newCrimsonForestDecorator());
		this.setColor(0xFA9418);
		fogSkyColor = 0xAD0808;
		this.topBlock = ModBlocks.NYLIUM.get();
		this.fillerBlock = Blocks.netherrack;

		spawnableMonsterList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 1, 2, 4));
	}
}