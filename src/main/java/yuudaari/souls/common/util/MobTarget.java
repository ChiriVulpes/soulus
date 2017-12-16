package yuudaari.souls.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class MobTarget {
	@Nullable
	public static String getMobTarget(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("essence_type", 8)) {
				return tag.getString("essence_type");
			}
			if (tag.hasKey("entity_type", 8)) {
				return tag.getString("entity_type");
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
		tag.setString("essence_type", mobTarget);
		return stack;
	}
}
