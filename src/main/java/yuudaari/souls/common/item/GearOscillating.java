package yuudaari.souls.common.item;

import net.minecraft.item.ItemStack;

public class GearOscillating extends SummonerUpgrade {
	public GearOscillating() {
		super("gear_oscillating", 16);
		glint = true;
	}

	public ItemStack getFilledStack() {
		return super.getItemStack();
	}
}