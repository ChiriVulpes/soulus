package yuudaari.souls.common.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import yuudaari.souls.common.util.Logger;
import yuudaari.souls.common.config.FieldSerializer;
import yuudaari.souls.common.config.Serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import yuudaari.souls.common.util.Range;
import java.util.Random;

public class OreVein {
	public String block;
	public String replace;
	public int chances = 4;
	public Range size = new Range(2, 6);
	public Range height = new Range(0, 255);
	public DimensionType dimension = DimensionType.OVERWORLD;

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

	public void generate(World world, Random random, int chunkX, int chunkZ) {
		if (world.provider.getDimensionType() == dimension) {
			int veinSize = size.min.intValue() + random.nextInt(size.max.intValue() - size.min.intValue());
			int heightRange = height.max.intValue() - height.min.intValue();

			WorldGenMinable gen = new WorldGenMinable(Block.getBlockFromName(block).getDefaultState(), veinSize,
					blockstate -> blockstate.equals(Block.getBlockFromName(replace).getDefaultState()));
			for (int i = 0; i < chances; i++) {
				int xRand = chunkX * 16 + random.nextInt(16);
				int yRand = random.nextInt(heightRange) + height.min.intValue();
				int zRand = chunkZ * 16 + random.nextInt(16);
				gen.generate(world, random, new BlockPos(xRand, yRand, zRand));
			}
		}
	}

	public static Serializer<OreVein> serializer;
	static {
		serializer = new Serializer<>(OreVein.class, "block", "replace", "chances");

		serializer.fieldHandlers.put("dimension",
				new FieldSerializer<DimensionType>(OreVein::serializeDimension, OreVein::deserializeDimension));
		serializer.fieldHandlers.put("size", Range.serializer);
		serializer.fieldHandlers.put("height", Range.serializer);
	}

	public static JsonElement serializeDimension(Object obj) {
		return new JsonPrimitive(((DimensionType) obj).getName());
	}

	public static Object deserializeDimension(JsonElement json, Object current) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
			Logger.warn("Dimension must be a string identifier");
			return null;
		}
		return DimensionType.byName(json.getAsString());
	}
}
