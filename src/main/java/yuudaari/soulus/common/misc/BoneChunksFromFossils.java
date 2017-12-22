package yuudaari.soulus.common.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.common.config.ManualSerializer;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.item.BoneChunk;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.Range;

@Mod.EventBusSubscriber
public class BoneChunksFromFossils {
	public static BoneChunksFromFossils INSTANCE = new BoneChunksFromFossils();

	public static ManualSerializer serializer = new ManualSerializer(BoneChunksFromFossils::serialize,
			BoneChunksFromFossils::deserialize);

	public static JsonElement serialize(Object obj) {
		JsonObject result = new JsonObject();

		BoneChunksFromFossils config = (BoneChunksFromFossils) obj;
		for (Map.Entry<String, FossilConfig> entry : config.fossils.entrySet()) {
			result.add(entry.getKey(), FossilConfig.serializer.serialize(entry.getValue()));
		}

		return result;
	}

	public static Object deserialize(JsonElement json, Object current) {
		if (json == null || !(json instanceof JsonObject)) {
			Logger.info("fossils", "Must be an object");
			return current;
		}

		JsonObject config = (JsonObject) json;

		BoneChunksFromFossils boneChunksFromFossils = (BoneChunksFromFossils) current;
		boneChunksFromFossils.fossils.clear();

		for (Map.Entry<String, JsonElement> fossilConfig : config.entrySet()) {
			boneChunksFromFossils.fossils.put(fossilConfig.getKey(),
					(FossilConfig) FossilConfig.serializer.deserialize(fossilConfig.getValue(), new FossilConfig()));
		}

		return boneChunksFromFossils;
	}

	public static class FossilConfig {
		public static Serializer<FossilConfig> serializer = new Serializer<>(FossilConfig.class, "min", "max");

		static {
			serializer.fieldHandlers.put("type",
					new ManualSerializer(boneType -> new JsonPrimitive(BoneType.getString((BoneType) boneType)),
							(boneTypeName, into) -> BoneType.getBoneType(boneTypeName.getAsString())));
		}

		public BoneType type;
		public int min;
		public int max;

		public FossilConfig() {
		}

		public FossilConfig(BoneType boneType, int min, int max) {
			this.type = boneType;
			this.min = min;
			this.max = max;
		}
	}

	public Map<String, FossilConfig> fossils = new HashMap<>();
	{
		fossils.put("soulus:fossil_dirt", new FossilConfig(BoneType.NORMAL, 2, 6));
		fossils.put("soulus:fossil_dirt_ender", new FossilConfig(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_dirt_frozen", new FossilConfig(BoneType.FROZEN, 2, 6));
		fossils.put("soulus:fossil_dirt_fungal", new FossilConfig(BoneType.FUNGAL, 2, 6));
		fossils.put("soulus:fossil_netherrack", new FossilConfig(BoneType.NETHER, 2, 6));
		fossils.put("soulus:fossil_netherrack_ender", new FossilConfig(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_sand", new FossilConfig(BoneType.DRY, 2, 6));
		fossils.put("soulus:fossil_sand_ender", new FossilConfig(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_sand_scale", new FossilConfig(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_end_stone", new FossilConfig(BoneType.ENDER, 2, 6));
	}

	@SubscribeEvent
	public static void onHarvest(HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			String blockId = event.getState().getBlock().getRegistryName().toString();
			if (INSTANCE.fossils.containsKey(blockId)) {

				FossilConfig fossilConfig = INSTANCE.fossils.get(blockId);
				List<ItemStack> drops = event.getDrops();
				drops.clear();

				BoneChunk boneChunk = BoneChunk.boneChunkTypes.get(fossilConfig.type);
				int count = new Range(fossilConfig.min * (1 + event.getFortuneLevel() / 3),
						fossilConfig.max * (1 + event.getFortuneLevel() / 3)).getInt(event.getWorld().rand);
				drops.add(boneChunk.getItemStack(count));
			}
		}
	}
}