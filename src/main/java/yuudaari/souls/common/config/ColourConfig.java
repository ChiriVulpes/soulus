package yuudaari.souls.common.config;

import net.minecraft.entity.EntityList;

public class ColourConfig {
	public int primaryColour;
	public int secondaryColour;

	public ColourConfig(int primaryColour, int secondaryColour) {
		this.primaryColour = primaryColour;
		this.secondaryColour = secondaryColour;
	}

	public ColourConfig(EntityList.EntityEggInfo eggInfo) {
		this.primaryColour = eggInfo.primaryColor;
		this.secondaryColour = eggInfo.secondaryColor;
	}
}