package yuudaari.soulus.common.config.misc;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.serializer.DefaultMapSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "misc/fossil_block_bone_chunk_drops", id = Soulus.MODID)
@Serializable
public class ConfigFossils {

	@Serialized(ConfigFossilSerializer.class) public Map<String, ConfigFossil> fossils = new HashMap<>();
	{
		fossils.put("soulus:fossil_dirt_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_dirt_frozen", new ConfigFossil(BoneType.FROZEN, 2, 6));
		fossils.put("soulus:fossil_dirt_fungal", new ConfigFossil(BoneType.FUNGAL, 2, 6));
		fossils.put("soulus:fossil_dirt", new ConfigFossil(BoneType.NORMAL, 2, 6));
		fossils.put("soulus:fossil_end_stone", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_gravel_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_netherrack_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_netherrack", new ConfigFossil(BoneType.NETHER, 2, 6));
		fossils.put("soulus:fossil_sand_ender", new ConfigFossil(BoneType.ENDER, 2, 6));
		fossils.put("soulus:fossil_sand_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_sand", new ConfigFossil(BoneType.DRY, 2, 6));
		fossils.put("soulus:fossil_sand_red_scale", new ConfigFossil(BoneType.SCALE, 2, 6));
		fossils.put("soulus:fossil_sand_red_dry", new ConfigFossil(BoneType.DRY, 2, 6));
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

		@Serialized(BoneType.Serializer.class) public BoneType type;
		@Serialized public int min;
		@Serialized public int max;

		public ConfigFossil () {}

		public ConfigFossil (BoneType boneType, int min, int max) {
			this.type = boneType;
			this.min = min;
			this.max = max;
		}
	}
}
