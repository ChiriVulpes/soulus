package yuudaari.soulus.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackMutable {

	private ItemStack stack;

	public ItemStackMutable () {
		this.stack = ItemStack.EMPTY;
	}

	public ItemStackMutable (final ItemStack stack) {
		this.stack = stack;
	}

	public ItemStack getImmutable () {
		return stack;
	}

	public void replace (final ItemStack stack) {
		this.stack = stack;
	}

	public boolean isEmpty () {
		return stack.isEmpty();
	}

	public ItemStack splitStack (final int amount) {
		return stack.splitStack(amount);
	}

	public Item getItem () {
		return stack.getItem();
	}

	public int getMaxStackSize () {
		return stack.getMaxStackSize();
	}

	public int getMetadata () {
		return stack.getMetadata();
	}

	public int getItemDamage () {
		return stack.getItemDamage();
	}

	public int getCount () {
		return stack.getCount();
	}

	public void shrink () {
		stack.shrink(1);
	}

	public void shrink (int quantity) {
		stack.shrink(quantity);
	}

	public ItemStack copy () {
		return stack.copy();
	}
}
