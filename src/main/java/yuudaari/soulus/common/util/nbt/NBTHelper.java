package yuudaari.soulus.common.util.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class NBTHelper {

	public static NBTHelper get (final NBTBase nbt) {
		if (nbt instanceof NBTTagCompound)
			return new NBTObject((NBTTagCompound) nbt);

		if (nbt instanceof NBTTagList)
			return new NBTList<>((NBTTagList) nbt);

		return new NBTPrimitive<>(nbt);
	}

	public static NBTObject get (final TileEntity te) {
		final NBTTagCompound nbt = new NBTTagCompound();
		te.writeToNBT(nbt);
		return new NBTObject(nbt);
	}

	public static NBTObject get (final ItemStack stack) {
		return get(stack, true);
	}

	public static NBTObject get (final ItemStack stack, final boolean createIfNotExist) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null && createIfNotExist)
			stack.setTagCompound(nbt = new NBTTagCompound());
		return new NBTObject(nbt);
	}

	public NBTObject getAsObject () {
		return (NBTObject) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends NBTHelper> NBTList<T> getAsList () {
		return (NBTList<T>) this;
	}

	@SuppressWarnings("unchecked")
	public <T> NBTPrimitive<T> getAsPrimitive () {
		return (NBTPrimitive<T>) this;
	}
}
