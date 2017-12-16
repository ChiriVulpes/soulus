package yuudaari.soulus.common.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.config.Serializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.util.Range;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OreVein {
	public String block;
	public String replace;
	public int chances = 4;
	public Range size = new Range(2, 6);
	public Range height = new Range(0, 255);
	public DimensionType dimension = null;
	public Type[] biomeTypesWhitelist = new Type[0];
	public Type[] biomeTypesBlacklist = new Type[0];

	public OreVein setBlock(String block) {
		this.block = block;
		return this;
	}

	public OreVein setToReplace(String block) {
		replace = block;
		return this;
	}

	public OreVein setDimension(DimensionType dimension) {
		this.dimension = dimension;
		return this;
	}

	public OreVein setSize(int min, int max) {
		this.size = new Range(min, max);
		return this;
	}

	public OreVein setChances(int chances) {
		this.chances = chances;
		return this;
	}

	public OreVein setHeight(int min, int max) {
		this.height = new Range(min, max);
		return this;
	}

	public OreVein setBiomes(Type... types) {
		this.biomeTypesWhitelist = types;
		return this;
	}

	public OreVein setBiomesBlacklist(Type... types) {
		this.biomeTypesBlacklist = types;
		return this;
	}

	public void generate(World world, Random random, int chunkX, int chunkZ) {
		if (dimension == null || world.provider.getDimensionType() == dimension) {
			int veinSize = size.min.intValue() + random.nextInt(size.max.intValue() - size.min.intValue());
			int heightRange = height.max.intValue() - height.min.intValue();

			WorldGenMinable gen = new WorldGenMinable(Block.getBlockFromName(block).getDefaultState(), veinSize,
					blockstate -> blockstate.equals(Block.getBlockFromName(replace).getDefaultState()));
			for (int i = 0; i < chances; i++) {
				int x = chunkX * 16 + random.nextInt(16);
				int y = random.nextInt(heightRange) + height.min.intValue();
				int z = chunkZ * 16 + random.nextInt(16);

				BlockPos pos = new BlockPos(x, y, z);

				if (biomeTypesWhitelist.length > 0) {
					Biome biome = world.getBiome(pos);

					boolean matched = false;
					for (Type type : biomeTypesWhitelist) {
						if (BiomeDictionary.hasType(biome, type)) {
							matched = true;
							break;
						}
					}
					if (!matched) {
						/*
						Logger.info(
								"Can't put '" + block + "' in biome '" + biome.getBiomeName() + "' (not whitelisted)");
						String out = "";
						for (Type type : biomeTypesWhitelist)
							out += type.getName() + ", ";
						Logger.info(out);
						*/
						// don't even bother, we failed once, it's likely we'll fail again
						// might as well speed up gen speeds slightly
						break;
					}
				}

				if (biomeTypesBlacklist.length > 0) {
					Biome biome = world.getBiome(pos);

					boolean matched = false;
					for (Type type : biomeTypesBlacklist) {
						if (BiomeDictionary.hasType(biome, type)) {
							matched = true;
							break;
						}
					}
					if (matched) {
						// Logger.info("Can't put '" + block + "' in biome '" + biome.getBiomeName() + "' (blacklisted)");
						// don't even bother, we failed once, it's likely we'll fail again
						// might as well speed up gen speeds slightly
						break;
					}
				}

				gen.generate(world, random, pos);
			}
		}
	}

	public static Serializer<OreVein> serializer;
	static {
		serializer = new Serializer<>(OreVein.class, "block", "replace", "chances");

		serializer.fieldHandlers.put("dimension",
				new ManualSerializer(OreVein::serializeDimension, OreVein::deserializeDimension));
		serializer.fieldHandlers.put("size", Range.serializer);
		serializer.fieldHandlers.put("height", Range.serializer);
		serializer.fieldHandlers.put("biomeTypesWhitelist",
				new ManualSerializer(OreVein::serializeBiomeTypes, OreVein::deserializeBiomeTypes));
		serializer.fieldHandlers.put("biomeTypesBlacklist",
				new ManualSerializer(OreVein::serializeBiomeTypes, OreVein::deserializeBiomeTypes));
	}

	public static JsonElement serializeBiomeTypes(Object from) {
		Type[] biomeTypes = (Type[]) from;

		JsonArray result = new JsonArray();
		for (Type type : biomeTypes) {
			result.add(type.getName().toLowerCase());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Object deserializeBiomeTypes(JsonElement from, Object current) {
		if (from == null || !from.isJsonArray()) {
			Logger.warn("Biome types must be an array");
			return current;
		}

		List<Type> result = new ArrayList<>();

		Map<String, Type> biomeTypes;
		try {
			Field f = Type.class.getDeclaredField("byName");
			f.setAccessible(true);
			biomeTypes = (Map<String, Type>) f.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Logger.error(e);
			return current;
		}

		JsonArray types = from.getAsJsonArray();

		deserialization: for (JsonElement type : types) {
			if (type == null || !type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString()) {
				Logger.warn("Biome type must be a string");
				continue;
			}

			String biome = type.getAsString();

			for (Map.Entry<String, Type> biomeType : biomeTypes.entrySet()) {
				String typeName = biomeType.getKey();
				if (typeName.equalsIgnoreCase(biome)) {
					result.add(biomeType.getValue());
					continue deserialization;
				}
			}

			Logger.warn("Biome type '" + biome + "' does not exist");
		}

		return result.toArray(new Type[result.size()]);
	}

	public static JsonElement serializeDimension(Object obj) {
		return obj == null ? JsonNull.INSTANCE : new JsonPrimitive(((DimensionType) obj).getName());
	}

	public static Object deserializeDimension(JsonElement json, Object current) {
		if (json == null || !json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
			return null;
		}
		return DimensionType.byName(json.getAsString());
	}
}
