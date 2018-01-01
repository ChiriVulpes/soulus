package yuudaari.soulus.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class EssenceType {
	@Nullable
	public static String getEssenceType(ItemStack stack) {
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

	public static ItemStack setEssenceType(ItemStack stack, String essenceType) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setString("essence_type", essenceType);
		return stack;
	}
}
