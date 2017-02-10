package com.kallgirl.souls.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class Essence extends Item {
	public Essence () {
		super("essence");
		setMaxStackSize(64);
	}
	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}
	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		NBTTagCompound stackData = new NBTTagCompound();
		stackData.setString("mobTarget", mobTarget);
		stack.setTagCompound(stackData);
		return stack;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack par1ItemStack) {
		NBTTagCompound tag = par1ItemStack.getTagCompound();
		return super.getUnlocalizedNameInefficiently(par1ItemStack).replace(
			":essence", ":essence." + (tag == null ? "unfocused" : tag.getString("mobTarget"))
		);
	}
}