package yuudaari.soulus.common.config.essence;

import net.minecraft.entity.EntityList;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ConfigColor {

	@Serialized public int primary;
	@Serialized public int secondary;

	public ConfigColor () {}

	public ConfigColor (int primaryColour, int secondaryColour) {
		setColors(primaryColour, secondaryColour);
	}

	public ConfigColor (EntityList.EntityEggInfo eggInfo) {
		setColors(eggInfo.primaryColor, eggInfo.secondaryColor);
	}

	public ConfigColor setColors (int primaryColour, int secondaryColour) {
		primary = primaryColour;
		secondary = secondaryColour;

		return this;
	}
}
