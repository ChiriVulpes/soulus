package yuudaari.soulus.common.config_old;

import net.minecraft.entity.EntityList;

public class ColorConfig {

	public static Serializer<ColorConfig> serializer = new Serializer<>(ColorConfig.class, "primary", "secondary");

	public int primary;
	public int secondary;

	public boolean wasSet = false;

	public ColorConfig () {}

	public ColorConfig (int primaryColour, int secondaryColour) {
		setColors(primaryColour, secondaryColour);
	}

	public ColorConfig (EntityList.EntityEggInfo eggInfo) {
		setColors(eggInfo.primaryColor, eggInfo.secondaryColor);
	}

	public ColorConfig setColors (int primaryColour, int secondaryColour) {
		primary = primaryColour;
		secondary = secondaryColour;
		wasSet = true;

		return this;
	}
}
