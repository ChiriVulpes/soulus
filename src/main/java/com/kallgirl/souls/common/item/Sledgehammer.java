package com.kallgirl.souls.common.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class Sledgehammer extends Item {
	private final Random random = new Random();
	public Sledgehammer() {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(256);
		String[] recipe = new String[]{
			"ISI",
			" S ",
			" S "
		};
		addRecipeShaped(
			recipe,
			'I', "ingotIron",
			'S', "stickWood"
		);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		stack.attemptDamageItem(1, random);
		ItemStack newStack = new ItemStack(stack.getItem());
		newStack.setItemDamage(stack.getItemDamage());
		return newStack;
	}
}
