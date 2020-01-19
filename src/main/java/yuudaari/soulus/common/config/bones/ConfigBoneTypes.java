package yuudaari.soulus.common.config.bones;

import java.util.ArrayList;
import java.util.List;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.util.serializer.CollectionSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@ConfigFile(file = "bones/bone_types", id = Soulus.MODID)
@Serializable
public class ConfigBoneTypes {

	@Serialized(value = BoneTypesSerializer.class) public List<ConfigBoneType> boneTypes;
	{
		boneTypes = new ArrayList<>();

		boneTypes.add(new ConfigBoneType("normal", "soulus:bone_normal", "soulus:bone_chunk_normal", 1.0 / 3.0));
		boneTypes.add(new ConfigBoneType("dry", "soulus:bone_dry", "soulus:bone_chunk_dry", 1.0 / 12.0));
		boneTypes.add(new ConfigBoneType("fungal", "soulus:bone_fungal", "soulus:bone_chunk_fungal", 1.0 / 12.0));
		boneTypes.add(new ConfigBoneType("frozen", "soulus:bone_frozen", "soulus:bone_chunk_frozen", 1.0 / 12.0));
		boneTypes.add(new ConfigBoneType("scale", "soulus:bone_scale", "soulus:bone_chunk_scale", 1.0 / 12.0));
		boneTypes.add(new ConfigBoneType("nether", "soulus:bone_nether", "soulus:bone_chunk_nether", 0.0));
		boneTypes.add(new ConfigBoneType("ender", "soulus:bone_ender", "soulus:bone_chunk_ender", 0.0));
	}

	public ConfigBoneType get (final String type) {
		return boneTypes.stream()
			.filter(config -> config.name.equalsIgnoreCase(type))
			.findFirst()
			.orElse(null);
	}

	public ConfigBoneType getFromBone (final String bone) {
		return boneTypes.stream()
			.filter(config -> config.itemBone.equalsIgnoreCase(bone))
			.findFirst()
			.orElse(null);
	}

	public ConfigBoneType getFromChunk (final String chunk) {
		return boneTypes.stream()
			.filter(config -> config.itemChunk.equalsIgnoreCase(chunk))
			.findFirst()
			.orElse(null);
	}

	public static class BoneTypesSerializer extends CollectionSerializer<ConfigBoneType> {

		@Override
		public Class<ConfigBoneType> getValueClass () {
			return ConfigBoneType.class;
		}
	}

}
