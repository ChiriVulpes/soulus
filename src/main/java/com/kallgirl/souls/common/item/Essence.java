package com.kallgirl.souls.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
		NBTTagCompound entityTag = new NBTTagCompound();
		entityTag.setString("id", mobTarget);
		NBTTagCompound stackData = new NBTTagCompound();
		stackData.setTag("EntityTag", entityTag);
		stack.setTagCompound(stackData);
		return stack;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently (@Nonnull ItemStack stack) {
		String mobTarget = "unfocused";
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("EntityTag", 10)) {
			tag = tag.getCompoundTag("EntityTag");
			if (tag.hasKey("id", 8)) {
				mobTarget = tag.getString("id");
			}
		}
		return super.getUnlocalizedNameInefficiently(stack).replace(
			":essence", ":essence." + mobTarget
		);
	}

	@Override
	public void init () {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			@ParametersAreNonnullByDefault
			@Override
			public int getColorFromItemstack (ItemStack stack, int tintIndex) {
				EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(ItemMonsterPlacer.getEntityIdFromItem(stack));
				return eggInfo == null ? -1 : (tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor);
			}
		}, this);
	}
}