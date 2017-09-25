package yuudaari.souls.common.world.generators;

import yuudaari.souls.common.util.Logger;
import yuudaari.souls.common.config.ManualSerializer;
import yuudaari.souls.common.world.ModGenerator;
import yuudaari.souls.common.world.OreVein;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.BiomeDictionary.Type;

public class GeneratorFossils extends ModGenerator {
	{
		this.setVeins(defaultVeins);
	}

	public static OreVein[] defaultVeins = new OreVein[] {
			new OreVein().setBlock("souls:fossil_dirt").setToReplace("minecraft:dirt").setSize(3, 7).setChances(300)
					.setBiomesBlacklist(Type.NETHER, Type.OCEAN, Type.END, Type.VOID),
			new OreVein().setBlock("souls:fossil_dirt_fungal").setToReplace("minecraft:dirt").setSize(3, 7)
					.setChances(300).setBiomes(Type.WET, Type.DENSE),
			new OreVein().setBlock("souls:fossil_dirt_frozen").setToReplace("minecraft:dirt").setSize(3, 7)
					.setChances(300).setBiomes(Type.COLD),
			new OreVein().setBlock("souls:fossil_dirt_ender").setToReplace("minecraft:dirt").setSize(2, 5)
					.setChances(100).setBiomesBlacklist(Type.NETHER, Type.OCEAN, Type.END, Type.VOID),
			new OreVein().setBlock("souls:fossil_sand").setToReplace("minecraft:sand").setSize(3, 7).setChances(300)
					.setBiomes(Type.HOT, Type.DRY).setBiomesBlacklist(Type.NETHER),
			new OreVein().setBlock("souls:fossil_sand_scale").setToReplace("minecraft:sand").setSize(3, 7)
					.setChances(300).setBiomes(Type.WATER, Type.HOT).setBiomesBlacklist(Type.NETHER),
			new OreVein().setBlock("souls:fossil_sand_ender").setToReplace("minecraft:sand").setSize(2, 5)
					.setBiomes(Type.HOT, Type.DRY).setBiomesBlacklist(Type.NETHER),
			new OreVein().setBlock("souls:fossil_netherrack").setToReplace("minecraft:netherrack").setSize(3, 7)
					.setChances(300).setBiomes(Type.NETHER),
			new OreVein().setBlock("souls:fossil_netherrack_ender").setToReplace("minecraft:netherrack").setSize(2, 5)
					.setChances(10).setBiomes(Type.NETHER) };

	public static final ManualSerializer serializer = new ManualSerializer(GeneratorFossils::serialize,
			GeneratorFossils::deserialize);

	public static JsonElement serialize(Object obj) {
		GeneratorFossils generator = (GeneratorFossils) obj;

		JsonArray result = new JsonArray();

		for (OreVein vein : generator.getVeins()) {
			result.add(OreVein.serializer.serialize(vein));
		}

		return result;
	}

	public static Object deserialize(JsonElement fossilVeinsElement, Object current) {
		List<OreVein> veinsList = new ArrayList<>();

		if (fossilVeinsElement == null || !fossilVeinsElement.isJsonArray()) {
			Logger.warn("Config must have 'fossil_veins' property set to an array");
			return null;
		}
		JsonArray fossilVeins = fossilVeinsElement.getAsJsonArray();

		for (JsonElement veinElement : fossilVeins) {
			if (!veinElement.isJsonObject()) {
				Logger.warn("OreVeins must be objects");
				continue;
			}
			OreVein vein = OreVein.serializer.deserialize(veinElement);
			if (vein == null)
				continue;
			veinsList.add(vein);
		}

		GeneratorFossils result = current == null ? new GeneratorFossils() : (GeneratorFossils) current;

		if (veinsList.size() > 0)
			result.setVeins(veinsList.toArray(new OreVein[veinsList.size()]));

		return result;
	}
}
