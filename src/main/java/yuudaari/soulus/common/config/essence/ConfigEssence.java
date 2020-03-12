package yuudaari.soulus.common.config.essence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.NullableField;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigEssence {

	@Serialized public String essence;
	@Serialized @NullableField public String name;
	@Serialized @NullableField public String[] spawnNames;
	@Serialized @NullableField public ConfigColor colors;
	@Serialized @NullableField public ConfigCreatureBone bones = null;
	@Serialized public double soulbookUsesMultiplier = 1;
	@Serialized public int soulbookQuantity = 16;
	@Serialized public Range soulbookFillXp = new Range(3, 5);
	@Serialized(DoubleMapSerializer.class) @NullableField public Map<String, Double> spawns;
	@Serialized(DoubleMapSerializer.class) @NullableField public Map<String, Double> spawnsMalice;
	@Serialized(LootMapSerializer.class) @NullableField public Map<String, ConfigCreatureLoot> loot;


	public ConfigEssence () {
	}

	public ConfigEssence (final String essence, final @Nullable ConfigCreatureBone bones) {
		this.essence = essence;
		this.bones = bones;
	}

	public ConfigEssence addSpawnChance (final String entity, final double chance) {
		if (spawns == null) spawns = new HashMap<>();
		spawns.put(entity, chance);
		return this;
	}

	public ConfigEssence addSpawnChanceMalice (final String entity, final double chance) {
		if (spawnsMalice == null) spawnsMalice = new HashMap<>();
		spawnsMalice.put(entity, chance);
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

	public ConfigEssence setSoulbookUsesMultiplier (final double multiplier) {
		this.soulbookUsesMultiplier = multiplier;
		return this;
	}

	public Set<String> getSpawnableCreatures () {
		final Set<String> spawnableCreatures = new HashSet<>();

		if (this.spawns != null)
			spawnableCreatures.addAll(this.spawns.keySet());

		if (this.spawnsMalice != null)
			spawnableCreatures.addAll(this.spawnsMalice.keySet());

		if (this.spawns == null && this.spawnsMalice == null)
			// if spawns & malice spawns are both null, this summoner can only spawn the named essence type
			spawnableCreatures.add(this.essence);

		return spawnableCreatures;
	}

	public static class DoubleMapSerializer extends DefaultMapSerializer.OfStringKeys<Double> {

		@Override
		public Class<Double> getValueClass () {
			return Double.class;
		}
	}

	public static class LootMapSerializer extends DefaultMapSerializer.OfStringKeys<ConfigCreatureLoot> {

		@Override
		public Class<ConfigCreatureLoot> getValueClass () {
			return ConfigCreatureLoot.class;
		}
	}
}
