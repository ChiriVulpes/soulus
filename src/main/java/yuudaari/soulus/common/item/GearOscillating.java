package yuudaari.soulus.common.item;

import net.minecraft.item.ItemStack;

public class GearOscillating extends SummonerUpgrade {
	public GearOscillating() {
		super("gear_oscillating", 16);
		glint = true;
		setHasDescription();
	}

	public ItemStack getFilledStack() {
		return super.getItemStack();
	}
}