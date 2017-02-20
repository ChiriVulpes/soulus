package com.kallgirl.souls.common.util;

import com.kallgirl.souls.common.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Map;

public class MobTarget {
	public static String fixMobTarget(String mobTarget) {
		for (Map.Entry<String, String> map : Config.EntityIdMap.entrySet()) {
			if (map.getValue().equals(mobTarget)) {
				return map.getKey();
			}
		}
		return mobTarget;
	}

	@Nullable
	public static String getMobTarget(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("EntityTag", 10)) {
			tag = tag.getCompoundTag("EntityTag");
			if (tag.hasKey("id", 8)) {
				return tag.getString("id");
			}
		}
		return null;
	}

	public static ItemStack setMobTarget(ItemStack stack, String mobTarget) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		if (tag.hasKey("EntityTag", 10)) {
			tag = tag.getCompoundTag("EntityTag");
		} else {
			NBTTagCompound entityTag = new NBTTagCompound();
			tag.setTag("EntityTag", entityTag);
			tag = entityTag;
		}
		tag.setString("id", mobTarget);
		return stack;
	}
}
