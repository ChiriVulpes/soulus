package yuudaari.souls.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.souls.common.util.ModItem;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class Sledgehammer extends ModItem {
	private final Random random = new Random();

	public Sledgehammer() {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(256);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		stack.attemptDamageItem(1, random, null);
		ItemStack newStack = new ItemStack(stack.getItem());
		newStack.setItemDamage(stack.getItemDamage());
		return newStack;
	}
}
