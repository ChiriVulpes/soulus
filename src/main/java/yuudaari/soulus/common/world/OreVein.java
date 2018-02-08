package yuudaari.soulus.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import yuudaari.soulus.common.util.BlockFromString;
import yuudaari.soulus.common.util.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.FieldSerializer;
import yuudaari.soulus.common.util.serializer.ListSerializer;
import yuudaari.soulus.common.util.serializer.NullableField;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unchecked")
@Serializable
public class OreVein {

	public static final Map<String, Type> BIOME_TYPES;
	static {
		try {
			Field f = Type.class.getDeclaredField("byName");
			f.setAccessible(true);

			BIOME_TYPES = (Map<String, Type>) f.get(null);

		} catch (final IllegalAccessException | NoSuchFieldException e) {
			Logger.error(e);
			throw new RuntimeException("Unable to generate biome types list");
		}
	}

	@Serialized public String block;
	@Serialized public String replace;
	@Serialized public int chances = 4;
	@Serialized public Range size = new Range(2, 6);
	@Serialized public Range height = new Range(0, 255);
	@Serialized(DimensionTypeSerializer.class) @NullableField public DimensionType dimension = null;
	@Serialized(BiomeTypeListSerializer.class) public List<Type> biomeTypesWhitelist = new ArrayList<>();
	@Serialized(BiomeTypeListSerializer.class) public List<Type> biomeTypesBlacklist = new ArrayList<>();

	public static class DimensionTypeSerializer extends FieldSerializer<DimensionType> {

		@Override
		public JsonElement serialize (Class<?> objectType, DimensionType object) {
			return object == null ? JsonNull.INSTANCE : new JsonPrimitive(object.getName());
		}

		@Override
		public DimensionType deserialize (Class<?> requestedType, JsonElement element) {
			return element.isJsonNull() ? null : DimensionType.byName(element.getAsString());
		}
	}

	public static class BiomeTypeListSerializer extends ListSerializer<Type> {

		@Override
		public JsonElement serializeValue (Type value) throws Exception {
			return new JsonPrimitive(value.getName());
		}

		@Override
		public Type deserializeValue (JsonElement value) throws Exception {
			return BIOME_TYPES.get(value.getAsString());
		}
	}

	public OreVein setBlock (String block) {
		this.block = block;
		return this;
	}

	public OreVein setToReplace (String block) {
		replace = block;
		return this;
	}

	public OreVein setDimension (DimensionType dimension) {
		this.dimension = dimension;
		return this;
	}

	public OreVein setSize (int min, int max) {
		this.size = new Range(min, max);
		return this;
	}

	public OreVein setChances (int chances) {
		this.chances = chances;
		return this;
	}

	public OreVein setHeight (int min, int max) {
		this.height = new Range(min, max);
		return this;
	}

	public OreVein setBiomes (Type... types) {
		this.biomeTypesWhitelist = Arrays.asList(types);
		return this;
	}

	public OreVein setBiomesBlacklist (Type... types) {
		this.biomeTypesBlacklist = Arrays.asList(types);
		return this;
	}

	public void generate (World world, Random random, int chunkX, int chunkZ) {

		if (chances <= 0) return;
		if (size.max <= size.min) return;
		if (height.max <= height.min) return;
		if (dimension != null && world.provider.getDimensionType() != dimension) return;


		int veinSize = size.min.intValue() + random.nextInt(size.max.intValue() - size.min.intValue());
		int heightRange = height.max.intValue() - height.min.intValue();

		IBlockState veinBlock = BlockFromString.get(block);
		IBlockState toReplace = BlockFromString.get(replace);

		if (veinBlock == null || toReplace == null) {
			Logger.error("Unable to generate vein of " + veinBlock + " in " + toReplace);
			return;
		}

		WorldGenMinable gen = new WorldGenMinable(veinBlock, veinSize, blockstate -> blockstate.equals(toReplace));
		for (int i = 0; i < chances; i++) {
			int x = chunkX * 16 + random.nextInt(16);
			int y = random.nextInt(heightRange) + height.min.intValue();
			int z = chunkZ * 16 + random.nextInt(16);

			BlockPos pos = new BlockPos(x, y, z);

			if (biomeTypesWhitelist.size() > 0) {
				Biome biome = world.getBiome(pos);

				if (!biomeTypesWhitelist.stream().anyMatch(type -> BiomeDictionary.hasType(biome, type))) {
					/*
					// Logger.info("Can't put '" + block + "' in biome '" + biome.getBiomeName() + "' (not whitelisted)");
					String out = "";
					for (Type type : biomeTypesWhitelist)
						out += type.getName() + ", ";
					// Logger.info(out);
					*/

					// don't even bother, we failed once, it's likely we'll fail again
					// might as well speed up gen speeds slightly
					break;
				}
			}

			if (biomeTypesBlacklist.size() > 0) {
				Biome biome = world.getBiome(pos);

				if (biomeTypesBlacklist.stream().anyMatch(type -> BiomeDictionary.hasType(biome, type))) {
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
