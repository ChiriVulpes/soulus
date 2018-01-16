package yuudaari.soulus.common.config.essence;

import yuudaari.soulus.common.util.BoneType;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreatureBone {

	@Serialized(BoneType.Serializer.class) public BoneType type;
	@Serialized public double dropWeight;

	public ConfigCreatureBone () {}

	public ConfigCreatureBone (BoneType type, double dropWeight) {
		this.type = type;
		this.dropWeight = dropWeight;
	}
}
