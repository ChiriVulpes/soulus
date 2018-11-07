package yuudaari.soulus.common.config.essence;

import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigCreatureBone {

	@Serialized public String type;
	@Serialized public double dropWeight;

	public ConfigCreatureBone () {}

	public ConfigCreatureBone (String boneType, double dropWeight) {
		this.type = boneType;
		this.dropWeight = dropWeight;
	}
}
