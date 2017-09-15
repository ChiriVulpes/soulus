package yuudaari.souls.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class MobTarget {
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
