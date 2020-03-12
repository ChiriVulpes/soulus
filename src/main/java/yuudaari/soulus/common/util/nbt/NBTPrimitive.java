package yuudaari.soulus.common.util.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTPrimitive<T> extends NBTHelper {

	private final NBTBase nbt;

	public NBTPrimitive (final NBTBase nbt) {
		this.nbt = nbt;
	}

	@SuppressWarnings("unchecked")
	public T get () {
		switch (NBTType.get(nbt)) {
			case BYTE:
				return (T) (Byte) ((NBTTagByte) nbt).getByte();
			case SHORT:
				return (T) (Short) ((NBTTagShort) nbt).getShort();
			case INT:
				return (T) (Integer) ((NBTTagInt) nbt).getInt();
			case LONG:
				return (T) (Long) ((NBTTagLong) nbt).getLong();
			case FLOAT:
				return (T) (Float) ((NBTTagFloat) nbt).getFloat();
			case DOUBLE:
				return (T) (Double) ((NBTTagDouble) nbt).getDouble();
			case BYTE_ARRAY:
				return (T) (byte[]) ((NBTTagByteArray) nbt).getByteArray();
			case STRING:
				return (T) (String) ((NBTTagString) nbt).getString();
			case LIST:
				return (T) (NBTTagList) nbt;
			case COMPOUND:
				return (T) (NBTTagCompound) nbt;
			case INT_ARRAY:
				return (T) (int[]) ((NBTTagIntArray) nbt).getIntArray();
			default:
				throw new IllegalStateException("Tried to get a primitive value when this NBT is not a primitive: " + nbt.toString());
		}
	}
}
