package yuudaari.soulus.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import yuudaari.soulus.common.util.BoneType;

public class EssenceConfig {
	public static class CreatureBoneConfig {
		public static Serializer<CreatureBoneConfig> serializer = new Serializer<>(CreatureBoneConfig.class,
				"dropWeight");
		static {
			serializer.fieldHandlers.put("type",
					new ManualSerializer(boneType -> new JsonPrimitive(BoneType.getString((BoneType) boneType)),
							(boneTypeName, into) -> BoneType.getBoneType(boneTypeName.getAsString())));
		}

		public BoneType type;
		public double dropWeight;

		public CreatureBoneConfig() {
		}

		public CreatureBoneConfig(BoneType type, double dropWeight) {
			this.type = type;
			this.dropWeight = dropWeight;
		}
	}

	public static class CreatureLootConfig {
		public static Serializer<CreatureLootConfig> serializer = new Serializer<>(CreatureLootConfig.class, "min",
				"max", "chance");

		public int min;
		public int max;
		public double chance;

		public CreatureLootConfig() {
		}

		public CreatureLootConfig(int min, int max, double chance) {
			this.min = min;
			this.max = max;
			this.chance = chance;
		}
	}

	public static Serializer<EssenceConfig> serializer = new Serializer<>(EssenceConfig.class, "essence",
			"soulbookQuantity");
	static {
		serializer.fieldHandlers.put("bones", new ManualSerializer(CreatureBoneConfig.serializer::serialize,
				CreatureBoneConfig.serializer::deserialize));
		serializer.fieldHandlers.put("loot",
				new ManualSerializer(EssenceConfig::serializeLoot, EssenceConfig::deserializeLoot));
		serializer.fieldHandlers.put("spawns",
				new ManualSerializer(EssenceConfig::serializeSpawns, EssenceConfig::deserializeSpawns));
	}

	public static JsonElement serializeLoot(Object obj) {
		@SuppressWarnings("unchecked")
		Map<String, CreatureLootConfig> loot = (Map<String, CreatureLootConfig>) obj;
		JsonObject result = new JsonObject();

		for (Map.Entry<String, CreatureLootConfig> entry : loot.entrySet()) {
			result.add(entry.getKey(), CreatureLootConfig.serializer.serialize(entry.getValue()));
		}

		return result;
	}

	public static Object deserializeLoot(JsonElement json, Object current) {
		JsonObject creatureConfig = (JsonObject) json;

		if (creatureConfig == null) {
			return current;
		}

		@SuppressWarnings("unchecked")
		Map<String, CreatureLootConfig> loot = (Map<String, CreatureLootConfig>) current;
		loot.clear();

		for (Map.Entry<String, JsonElement> entry : creatureConfig.entrySet()) {
			loot.put(entry.getKey(), (CreatureLootConfig) CreatureLootConfig.serializer.deserialize(entry.getValue(),
					new CreatureLootConfig()));
		}

		return loot;
	}

	public static JsonElement serializeSpawns(Object obj) {
		@SuppressWarnings("unchecked")
		Map<String, Double> spawns = (Map<String, Double>) obj;
		JsonObject result = new JsonObject();

		for (Map.Entry<String, Double> entry : spawns.entrySet()) {
			result.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
		}

		return result;
	}

	public static Object deserializeSpawns(JsonElement json, Object current) {
		JsonObject spawnsConfig = (JsonObject) json;

		if (spawnsConfig == null) {
			return current;
		}

		@SuppressWarnings("unchecked")
		Map<String, Double> spawns = (Map<String, Double>) current;
		spawns.clear();

		for (Map.Entry<String, JsonElement> entry : spawnsConfig.entrySet()) {
			spawns.put(entry.getKey(), entry.getValue().getAsDouble());
		}

		return spawns;
	}

	public String essence;
	public Map<String, Double> spawns = new HashMap<>();
	public Map<String, CreatureLootConfig> loot = new HashMap<>();
	public CreatureBoneConfig bones = new CreatureBoneConfig();
	public int soulbookQuantity = 16;

	public EssenceConfig() {
	}

	public EssenceConfig(String essence, CreatureBoneConfig bones) {
		this.essence = essence;
		this.bones = bones;
	}

	public EssenceConfig addSpawnChance(String entity, double chance) {
		spawns.put(entity, chance);
		return this;
	}

	public EssenceConfig addLoot(int min, int max, double chance) {
		loot.put(this.essence, new CreatureLootConfig(min, max, chance));
		return this;
	}

	public EssenceConfig addLoot(String entity, int min, int max, double chance) {
		loot.put(entity, new CreatureLootConfig(min, max, chance));
		return this;
	}

	public static List<EssenceConfig> getDefaultCreatureConfigs() {
		List<EssenceConfig> creatureConfigs = new ArrayList<>();

		// normal
		creatureConfigs
				.add(new EssenceConfig("minecraft:bat", new CreatureBoneConfig(BoneType.NORMAL, 1)).addLoot(1, 1, 0.5));
		creatureConfigs.add(
				new EssenceConfig("minecraft:chicken", new CreatureBoneConfig(BoneType.NORMAL, 8)).addLoot(1, 1, 0.5));
		creatureConfigs
				.add(new EssenceConfig("minecraft:cow", new CreatureBoneConfig(BoneType.NORMAL, 8)).addLoot(1, 3, 0.8));
		creatureConfigs
				.add(new EssenceConfig("minecraft:pig", new CreatureBoneConfig(BoneType.NORMAL, 6)).addLoot(1, 2, 0.8));
		creatureConfigs.add(
				new EssenceConfig("minecraft:rabbit", new CreatureBoneConfig(BoneType.NORMAL, 4)).addLoot(1, 1, 0.5));
		creatureConfigs.add(
				new EssenceConfig("minecraft:sheep", new CreatureBoneConfig(BoneType.NORMAL, 6)).addLoot(1, 3, 0.8));
		creatureConfigs.add(new EssenceConfig("minecraft:skeleton", new CreatureBoneConfig(BoneType.NORMAL, 3)));
		creatureConfigs.add(new EssenceConfig("minecraft:spider", new CreatureBoneConfig(BoneType.NORMAL, 5)));
		creatureConfigs.add(
				new EssenceConfig("minecraft:villager", new CreatureBoneConfig(BoneType.NORMAL, 1)).addLoot(1, 4, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.NORMAL, 5)));

		// dry
		creatureConfigs.add(new EssenceConfig("minecraft:cave_spider", new CreatureBoneConfig(BoneType.DRY, 1)));
		creatureConfigs
				.add(new EssenceConfig("minecraft:horse", new CreatureBoneConfig(BoneType.DRY, 1)).addLoot(2, 5, 1));
		creatureConfigs
				.add(new EssenceConfig("minecraft:husk", new CreatureBoneConfig(BoneType.DRY, 5)).addLoot(1, 3, 0.6));
		creatureConfigs
				.add(new EssenceConfig("minecraft:llama", new CreatureBoneConfig(BoneType.DRY, 10)).addLoot(1, 4, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.DRY, 30)));

		// fungal
		creatureConfigs.add(
				new EssenceConfig("minecraft:mooshroom", new CreatureBoneConfig(BoneType.FUNGAL, 1)).addLoot(1, 4, 1));
		creatureConfigs.add(
				new EssenceConfig("minecraft:ocelot", new CreatureBoneConfig(BoneType.FUNGAL, 10)).addLoot(1, 2, 0.5));
		creatureConfigs.add(
				new EssenceConfig("minecraft:parrot", new CreatureBoneConfig(BoneType.FUNGAL, 10)).addLoot(1, 1, 0.5));
		creatureConfigs
				.add(new EssenceConfig("minecraft:vindication_illager", new CreatureBoneConfig(BoneType.FUNGAL, 5))
						.addSpawnChance("minecraft:vindication_illager", 10)
						.addSpawnChance("minecraft:evocation_illager", 1)
						.addLoot("minecraft:vindication_illager", 1, 4, 1)
						.addLoot("minecraft:evocation_illager", 1, 4, 1));
		creatureConfigs.add(
				new EssenceConfig("minecraft:witch", new CreatureBoneConfig(BoneType.FUNGAL, 5)).addLoot(1, 4, 0.6));
		creatureConfigs.add(new EssenceConfig("minecraft:zombie", new CreatureBoneConfig(BoneType.FUNGAL, 20))
				.addSpawnChance("minecraft:zombie", 100).addSpawnChance("minecraft:zombie_villager", 10)
				.addSpawnChance("minecraft:zombie_horse", 1).addLoot("minecraft:zombie", 1, 3, 0.5)
				.addLoot("minecraft:zombie_villager", 1, 4, 0.8).addLoot("minecraft:zombie_horse", 2, 5, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.FUNGAL, 20)));

		// frozen
		creatureConfigs.add(new EssenceConfig("minecraft:snowman", new CreatureBoneConfig(BoneType.FROZEN, 1)));
		creatureConfigs
				.add(new EssenceConfig("minecraft:stray", new CreatureBoneConfig(BoneType.FROZEN, 1)).addLoot(2, 6, 1));
		creatureConfigs.add(
				new EssenceConfig("minecraft:wolf", new CreatureBoneConfig(BoneType.FROZEN, 5)).addLoot(1, 2, 0.5));
		creatureConfigs.add(new EssenceConfig("minecraft:polar_bear", new CreatureBoneConfig(BoneType.FROZEN, 10))
				.addLoot(2, 5, 0.6));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.FROZEN, 10)));

		// scale
		creatureConfigs.add(new EssenceConfig("minecraft:silverfish", new CreatureBoneConfig(BoneType.SCALE, 1))
				.addLoot(1, 1, 0.4));
		creatureConfigs.add(
				new EssenceConfig("minecraft:squid", new CreatureBoneConfig(BoneType.SCALE, 20)).addLoot(1, 2, 0.6));
		creatureConfigs.add(
				new EssenceConfig("minecraft:guardian", new CreatureBoneConfig(BoneType.SCALE, 1)).addLoot(2, 4, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.SCALE, 10)));

		// nether
		creatureConfigs.add(new EssenceConfig("minecraft:zombie_pigman", new CreatureBoneConfig(BoneType.NETHER, 20))
				.addLoot(1, 4, 0.5));
		creatureConfigs.add(
				new EssenceConfig("minecraft:blaze", new CreatureBoneConfig(BoneType.NETHER, 3)).addLoot(1, 3, 0.6));
		creatureConfigs.add(new EssenceConfig("minecraft:wither_skeleton", new CreatureBoneConfig(BoneType.NETHER, 1))
				.addLoot(2, 6, 1));
		creatureConfigs.add(
				new EssenceConfig("minecraft:ghast", new CreatureBoneConfig(BoneType.NETHER, 1)).addLoot(4, 10, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.NETHER, 10)));

		// ender
		creatureConfigs.add(new EssenceConfig("minecraft:shulker", new CreatureBoneConfig(BoneType.ENDER, 1)));
		creatureConfigs.add(new EssenceConfig("minecraft:endermite", new CreatureBoneConfig(BoneType.ENDER, 15))
				.addLoot(1, 1, 0.4));
		creatureConfigs.add(
				new EssenceConfig("minecraft:creeper", new CreatureBoneConfig(BoneType.ENDER, 10)).addLoot(1, 3, 0.8));
		creatureConfigs.add(
				new EssenceConfig("minecraft:enderman", new CreatureBoneConfig(BoneType.ENDER, 2)).addLoot(2, 6, 1));
		creatureConfigs.add(new EssenceConfig("NONE", new CreatureBoneConfig(BoneType.ENDER, 20)));

		return creatureConfigs;
	}
}