package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.Range;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/fossil_block_bone_chunk_drops", id = Soulus.MODID)
@Serializable
public class ConfigFossils {

	@Serialized(ConfigFossilSerializer.class) public Map<String, ConfigFossil> fossils = new HashMap<>();
	{
		fossils.put("soulus:fossil_dirt_ender", new ConfigFossil("ENDER").setXP(3, 7));
		fossils.put("soulus:fossil_dirt_frozen", new ConfigFossil("FROZEN"));
		fossils.put("soulus:fossil_dirt_fungal", new ConfigFossil("FUNGAL"));
		fossils.put("soulus:fossil_dirt", new ConfigFossil("NORMAL"));
		fossils.put("soulus:fossil_end_stone", new ConfigFossil("ENDER").setXP(3, 7));
		fossils.put("soulus:fossil_gravel_scale", new ConfigFossil("SCALE").setXP(1, 4));
		fossils.put("soulus:fossil_netherrack_ender", new ConfigFossil("ENDER").setXP(3, 7));
		fossils.put("soulus:fossil_netherrack", new ConfigFossil("NETHER").setXP(2, 5));
		fossils.put("soulus:fossil_sand_ender", new ConfigFossil("ENDER").setXP(3, 7));
		fossils.put("soulus:fossil_sand_scale", new ConfigFossil("SCALE"));
		fossils.put("soulus:fossil_sand", new ConfigFossil("DRY"));
		fossils.put("soulus:fossil_sand_red_scale", new ConfigFossil("SCALE"));
		fossils.put("soulus:fossil_sand_red_dry", new ConfigFossil("DRY"));
	}

	public ConfigFossil get (String id) {
		return fossils.get(id);
	}

	public static class ConfigFossilSerializer extends DefaultMapSerializer<ConfigFossil> {

		@Override
		public Class<ConfigFossil> getValueClass () {
			return ConfigFossil.class;
		}
	}

	@Serializable
	public static class ConfigFossil {

		@Serialized public String type;
		@Serialized public Range chunks = new Range(2, 6);
		@Serialized public double fullBoneChance = 0.03;
		@Serialized public Range xp = new Range(0, 2);

		public ConfigFossil () {
		}

		public ConfigFossil (String boneType) {
			this.type = boneType;
		}

		public ConfigFossil (String boneType, int min, int max) {
			this.type = boneType;
			this.chunks = new Range(min, max);
		}

		public ConfigFossil (String boneType, int min, int max, double fullBoneChance, Range xp) {
			this.type = boneType;
			this.chunks = new Range(min, max);
			this.fullBoneChance = fullBoneChance;
			this.xp = xp;
		}

		public ConfigFossil setXP (final Range xp) {
			this.xp = xp;
			return this;
		}

		public ConfigFossil setXP (final int min, final int max) {
			this.xp = new Range(min, max);
			return this;
		}
	}
}
