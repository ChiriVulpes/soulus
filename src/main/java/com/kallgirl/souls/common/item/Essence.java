package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.Config;
import com.kallgirl.souls.common.util.MobTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
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
			":essence", ":essence." + MobTarget.fixMobTarget(mobTarget)
		);
	}

	@Override
	public void init () {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((ItemStack stack, int tintIndex) -> {
			String mobTarget = MobTarget.getMobTarget(stack);
			if (mobTarget == null) return -1;
			Config.SoulInfo soulInfo = Config.getSoulInfo(mobTarget, false);
			if (soulInfo == null) return -1;
			Config.ColourInfo colourInfo = soulInfo.colourInfo;
			if (colourInfo == null) {
				EntityList.EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(mobTarget);
				if (eggInfo == null) return -1;
				colourInfo = new Config.ColourInfo(eggInfo);
			}
			return tintIndex == 0 ? colourInfo.primaryColour : colourInfo.secondaryColour;
		}, this);
	}
}