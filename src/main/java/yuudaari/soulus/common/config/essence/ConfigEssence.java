package yuudaari.soulus.common.config.essence;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.NullableField;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigEssence {

	@Serialized public String essence;
	@Serialized @NullableField public String name;
	@Serialized @NullableField public ConfigColor colors;
	@Serialized @NullableField public ConfigCreatureBone bones = null;
	@Serialized public int soulbookQuantity = 16;
	@Serialized(DoubleMapSerializer.class) @NullableField public Map<String, Double> spawns;
	@Serialized(LootMapSerializer.class) @NullableField public Map<String, ConfigCreatureLoot> loot;


	public ConfigEssence () {}

	public ConfigEssence (String essence, @Nullable ConfigCreatureBone bones) {
		this.essence = essence;
		this.bones = bones;
	}

	public ConfigEssence addSpawnChance (String entity, double chance) {
		if (spawns == null) spawns = new HashMap<>();
		spawns.put(entity, chance);
		return this;
	}

	public ConfigEssence addLoot (int min, int max, double chance) {
		if (loot == null) loot = new HashMap<>();
		loot.put(this.essence, new ConfigCreatureLoot(min, max, chance));
		return this;
	}

	public ConfigEssence addLoot (String entity, int min, int max, double chance) {
		if (loot == null) loot = new HashMap<>();
		loot.put(entity, new ConfigCreatureLoot(min, max, chance));
		return this;
	}

	public ConfigEssence setColor (int color1, int color2) {
		this.colors = new ConfigColor(color1, color2);
		return this;
	}

	public static class DoubleMapSerializer extends DefaultMapSerializer<Double> {

		@Override
		public Class<Double> getValueClass () {
			return Double.class;
		}
	}

	public static class LootMapSerializer extends DefaultMapSerializer<ConfigCreatureLoot> {

		@Override
		public Class<ConfigCreatureLoot> getValueClass () {
			return ConfigCreatureLoot.class;
		}
	}
}
