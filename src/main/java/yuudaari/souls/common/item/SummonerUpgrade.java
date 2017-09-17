package yuudaari.souls.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.souls.common.util.ModItem;

public abstract class SummonerUpgrade extends ModItem {
	public SummonerUpgrade(String name) {
		super(name);
	}

	public SummonerUpgrade(String name, Integer maxStackSize) {
		super(name, maxStackSize);
	}

	public abstract ItemStack getFilledStack();
}