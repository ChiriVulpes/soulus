package yuudaari.soulus.common.config.world;

import net.minecraftforge.common.BiomeDictionary.Type;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.config.ConfigProfile;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.common.world.OreVein;

@ConfigFile(file = "world/veins/veins", id = Soulus.MODID, profile = "enabled")
@Serializable
public class ConfigOreVeins {

	@ConfigProfile public static ConfigOreVeins disabled = new ConfigOreVeins();
	@ConfigProfile public static ConfigOreVeins enabled = new ConfigOreVeins(new OreVein[] {
		new OreVein().setBlock("soulus:fossil_dirt")
			.setToReplace("minecraft:dirt")
			.setSize(3, 7)
			.setChances(300)
			.setBiomesBlacklist(Type.NETHER, Type.OCEAN, Type.END, Type.VOID),
		new OreVein()
			.setBlock("soulus:fossil_dirt_fungal")
			.setToReplace("minecraft:dirt")
			.setSize(3, 7)
			.setChances(300)
			.setBiomes(Type.WET, Type.DENSE),
		new OreVein().setBlock("soulus:fossil_dirt_frozen")
			.setToReplace("minecraft:dirt")
			.setSize(3, 7)
			.setChances(300)
			.setBiomes(Type.COLD),
		new OreVein().setBlock("soulus:fossil_dirt_ender")
			.setToReplace("minecraft:dirt")
			.setSize(2, 5)
			.setChances(100)
			.setBiomesBlacklist(Type.NETHER, Type.OCEAN, Type.END, Type.VOID),
		new OreVein()
			.setBlock("soulus:fossil_gravel_scale")
			.setToReplace("minecraft:gravel")
			.setSize(3, 7)
			.setChances(30)
			.setBiomes(Type.OCEAN),
		new OreVein().setBlock("soulus:fossil_sand")
			.setToReplace("minecraft:sand@0")
			.setSize(3, 7)
			.setChances(50)
			.setBiomes(Type.HOT, Type.DRY)
			.setBiomesBlacklist(Type.NETHER),
		new OreVein().setBlock("soulus:fossil_sand_scale")
			.setToReplace("minecraft:sand@0")
			.setSize(3, 7)
			.setChances(30)
			.setBiomes(Type.WATER, Type.HOT)
			.setBiomesBlacklist(Type.NETHER),
		new OreVein().setBlock("soulus:fossil_sand_ender")
			.setToReplace("minecraft:sand@0")
			.setSize(2, 5)
			.setBiomes(Type.HOT, Type.DRY)
			.setBiomesBlacklist(Type.NETHER),
		new OreVein()
			.setBlock("soulus:fossil_sand_red_dry")
			.setToReplace("minecraft:sand@1")
			.setSize(3, 7)
			.setChances(50)
			.setBiomes(Type.HOT, Type.DRY)
			.setBiomesBlacklist(Type.NETHER),
		new OreVein()
			.setBlock("soulus:fossil_sand_red_scale")
			.setToReplace("minecraft:sand@1")
			.setSize(3, 7)
			.setChances(30)
			.setBiomes(Type.HOT, Type.DRY)
			.setBiomesBlacklist(Type.NETHER),
		new OreVein()
			.setBlock("soulus:fossil_netherrack")
			.setToReplace("minecraft:netherrack")
			.setSize(3, 7)
			.setChances(300)
			.setBiomes(Type.NETHER),
		new OreVein()
			.setBlock("soulus:fossil_netherrack_ender")
			.setToReplace("minecraft:netherrack")
			.setSize(2, 5)
			.setChances(10)
			.setBiomes(Type.NETHER),
		new OreVein()
			.setBlock("soulus:fossil_end_stone")
			.setToReplace("minecraft:end_stone")
			.setSize(4, 9)
			.setChances(300)
			.setBiomes(Type.END)
	});

	@Serialized public OreVein[] veins;

	public ConfigOreVeins () {
		veins = new OreVein[0];
	}

	public ConfigOreVeins (OreVein[] veins) {
		this.veins = veins;
	}

}
