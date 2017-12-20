package yuudaari.soulus.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import yuudaari.soulus.common.util.BoneType;

public class CreatureConfig {
	public static class CreatureBoneConfig {
		public static Serializer<CreatureBoneConfig> serializer = new Serializer<>(CreatureBoneConfig.class, "min",
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

	public static Serializer<CreatureConfig> serializer = new Serializer<>(CreatureConfig.class, "essence",
			"soulbookQuantity");
	static {
		serializer.fieldHandlers.put("bones", new ManualSerializer(CreatureBoneConfig.serializer::serialize,
				CreatureBoneConfig.serializer::deserialize));
		serializer.fieldHandlers.put("loot",
				new ManualSerializer(CreatureConfig::serializeLoot, CreatureConfig::deserializeLoot));
		serializer.fieldHandlers.put("spawns",
				new ManualSerializer(CreatureConfig::serializeSpawns, CreatureConfig::deserializeSpawns));
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

	public CreatureConfig() {
	}

	public CreatureConfig(String essence, CreatureBoneConfig bones) {
		this.essence = essence;
		this.bones = bones;
	}

	public CreatureConfig addSpawnChance(String entity, double chance) {
		spawns.put(entity, chance);
		return this;
	}

	public CreatureConfig addLoot(int min, int max, double chance) {
		loot.put(this.essence, new CreatureLootConfig(min, max, chance));
		return this;
	}

	public CreatureConfig addLoot(String entity, int min, int max, double chance) {
		loot.put(entity, new CreatureLootConfig(min, max, chance));
		return this;
	}

	public static List<CreatureConfig> getDefaultCreatureConfigs() {
		List<CreatureConfig> creatureConfigs = new ArrayList<>();

		// normal
		creatureConfigs.add(
				new CreatureConfig("minecraft:bat", new CreatureBoneConfig(BoneType.NORMAL, 1)).addLoot(1, 1, 0.5));
		creatureConfigs.add(
				new CreatureConfig("minecraft:chicken", new CreatureBoneConfig(BoneType.NORMAL, 8)).addLoot(1, 1, 0.5));
		creatureConfigs.add(
				new CreatureConfig("minecraft:cow", new CreatureBoneConfig(BoneType.NORMAL, 8)).addLoot(1, 3, 0.8));
		creatureConfigs.add(
				new CreatureConfig("minecraft:pig", new CreatureBoneConfig(BoneType.NORMAL, 6)).addLoot(1, 2, 0.8));
		creatureConfigs.add(
				new CreatureConfig("minecraft:rabbit", new CreatureBoneConfig(BoneType.NORMAL, 4)).addLoot(1, 1, 0.5));
		creatureConfigs.add(
				new CreatureConfig("minecraft:sheep", new CreatureBoneConfig(BoneType.NORMAL, 6)).addLoot(1, 3, 0.8));
		creatureConfigs.add(new CreatureConfig("minecraft:skeleton", new CreatureBoneConfig(BoneType.NORMAL, 3)));
		creatureConfigs.add(new CreatureConfig("minecraft:spider", new CreatureBoneConfig(BoneType.NORMAL, 5)));
		creatureConfigs.add(
				new CreatureConfig("minecraft:villager", new CreatureBoneConfig(BoneType.NORMAL, 1)).addLoot(1, 4, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.NORMAL, 5)));

		// dry
		creatureConfigs.add(new CreatureConfig("minecraft:cave_spider", new CreatureBoneConfig(BoneType.DRY, 1)));
		creatureConfigs
				.add(new CreatureConfig("minecraft:horse", new CreatureBoneConfig(BoneType.DRY, 1)).addLoot(2, 5, 1));
		creatureConfigs
				.add(new CreatureConfig("minecraft:husk", new CreatureBoneConfig(BoneType.DRY, 5)).addLoot(1, 3, 0.6));
		creatureConfigs
				.add(new CreatureConfig("minecraft:llama", new CreatureBoneConfig(BoneType.DRY, 10)).addLoot(1, 4, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.DRY, 30)));

		// fungal
		creatureConfigs.add(
				new CreatureConfig("minecraft:mooshroom", new CreatureBoneConfig(BoneType.FUNGAL, 1)).addLoot(1, 4, 1));
		creatureConfigs.add(
				new CreatureConfig("minecraft:ocelot", new CreatureBoneConfig(BoneType.FUNGAL, 10)).addLoot(1, 2, 0.5));
		creatureConfigs.add(
				new CreatureConfig("minecraft:parrot", new CreatureBoneConfig(BoneType.FUNGAL, 10)).addLoot(1, 1, 0.5));
		creatureConfigs
				.add(new CreatureConfig("minecraft:vindication_illager", new CreatureBoneConfig(BoneType.FUNGAL, 5))
						.addSpawnChance("minecraft:vindication_illager", 10)
						.addSpawnChance("minecraft:evocation_illager", 1)
						.addLoot("minecraft:vindication_illager", 1, 4, 1)
						.addLoot("minecraft:evocation_illager", 1, 4, 1));
		creatureConfigs.add(
				new CreatureConfig("minecraft:witch", new CreatureBoneConfig(BoneType.FUNGAL, 5)).addLoot(1, 4, 0.6));
		creatureConfigs.add(new CreatureConfig("minecraft:zombie", new CreatureBoneConfig(BoneType.FUNGAL, 20))
				.addSpawnChance("minecraft:zombie", 100).addSpawnChance("minecraft:zombie_villager", 10)
				.addSpawnChance("minecraft:zombie_horse", 1).addLoot("minecraft:zombie", 1, 3, 0.5)
				.addLoot("minecraft:zombie_villager", 1, 4, 0.8).addLoot("minecraft:zombie_horse", 2, 5, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.FUNGAL, 20)));

		// frozen
		creatureConfigs.add(new CreatureConfig("minecraft:snowman", new CreatureBoneConfig(BoneType.FROZEN, 1)));
		creatureConfigs.add(
				new CreatureConfig("minecraft:stray", new CreatureBoneConfig(BoneType.FROZEN, 1)).addLoot(2, 6, 1));
		creatureConfigs.add(
				new CreatureConfig("minecraft:wolf", new CreatureBoneConfig(BoneType.FROZEN, 5)).addLoot(1, 2, 0.5));
		creatureConfigs.add(new CreatureConfig("minecraft:polar_bear", new CreatureBoneConfig(BoneType.FROZEN, 10))
				.addLoot(2, 5, 0.6));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.FROZEN, 10)));

		// scale
		creatureConfigs.add(new CreatureConfig("minecraft:silverfish", new CreatureBoneConfig(BoneType.SCALE, 1))
				.addLoot(1, 1, 0.4));
		creatureConfigs.add(
				new CreatureConfig("minecraft:squid", new CreatureBoneConfig(BoneType.SCALE, 20)).addLoot(1, 2, 0.6));
		creatureConfigs.add(
				new CreatureConfig("minecraft:guardian", new CreatureBoneConfig(BoneType.SCALE, 1)).addLoot(2, 4, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.SCALE, 10)));

		// nether
		creatureConfigs.add(new CreatureConfig("minecraft:zombie_pigman", new CreatureBoneConfig(BoneType.NETHER, 20))
				.addLoot(1, 4, 0.5));
		creatureConfigs.add(
				new CreatureConfig("minecraft:blaze", new CreatureBoneConfig(BoneType.NETHER, 3)).addLoot(1, 3, 0.6));
		creatureConfigs.add(new CreatureConfig("minecraft:wither_skeleton", new CreatureBoneConfig(BoneType.NETHER, 1))
				.addLoot(2, 6, 1));
		creatureConfigs.add(
				new CreatureConfig("minecraft:ghast", new CreatureBoneConfig(BoneType.NETHER, 1)).addLoot(4, 10, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.NETHER, 10)));

		// ender
		creatureConfigs.add(new CreatureConfig("minecraft:shulker", new CreatureBoneConfig(BoneType.ENDER, 1)));
		creatureConfigs.add(new CreatureConfig("minecraft:endermite", new CreatureBoneConfig(BoneType.ENDER, 15))
				.addLoot(1, 1, 0.4));
		creatureConfigs.add(
				new CreatureConfig("minecraft:creeper", new CreatureBoneConfig(BoneType.ENDER, 10)).addLoot(1, 3, 0.8));
		creatureConfigs.add(
				new CreatureConfig("minecraft:enderman", new CreatureBoneConfig(BoneType.ENDER, 2)).addLoot(2, 6, 1));
		creatureConfigs.add(new CreatureConfig("NONE", new CreatureBoneConfig(BoneType.ENDER, 20)));

		return creatureConfigs;
	}
}