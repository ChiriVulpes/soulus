package yuudaari.soulus.common.util;

import net.minecraft.nbt.NBTTagCompound;
/*
 * import net.minecraft.nbt.NBTBase; import net.minecraft.nbt.NBTTagList; import net.minecraft.nbt.NBTTagString; import
 * java.util.ArrayList; import java.util.List; import java.util.Set;
 */

public class NBTHelper {

	public NBTTagCompound nbt;

	/*
	public static enum Tag {
		END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY
	}
	
	public static Tag[] TagMap;
	
	static {
		TagMap = Tag.values();
	}
	*/

	public NBTHelper () {
		this.nbt = new NBTTagCompound();
	}

	public NBTHelper (NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	/*
	public boolean hasTag(String key, Tag tagType) {
		return this.nbt.hasKey(key, tagType.ordinal());
	}
	
	public byte getByte(String key) {
		return this.nbt.getByte(key);
	}
	
	public short getShort(String key) {
		return this.nbt.getShort(key);
	}
	
	public int getInteger(String key) {
		return this.nbt.getInteger(key);
	}
	
	public long getLong(String key) {
		return this.nbt.getLong(key);
	}
	
	public float getFloat(String key) {
		return this.nbt.getFloat(key);
	}
	
	public double getDouble(String key) {
		return this.nbt.getDouble(key);
	}
	
	public byte[] getByteArray(String key) {
		return this.nbt.getByteArray(key);
	}
	
	public String getString(String key) {
		return this.nbt.getString(key);
	}
	
	public String[] getStringArray(String key) {
		List<String> result = new ArrayList<>();
		NBTTagList value = (NBTTagList) this.nbt.getTag(key);
		for (NBTBase s : value) {
			if (s instanceof NBTTagString) {
				result.add(((NBTTagString) s).getString());
			}
		}
	
		return result.toArray(new String[0]);
	}
	
	public NBTTagList getList(String key) {
		return (NBTTagList) this.nbt.getTag(key);
	}
	
	public NBTTagCompound getTag(String key) {
		return this.nbt.getCompoundTag(key);
	}
	
	public int[] getIntArray(String key) {
		return this.nbt.getIntArray(key);
	}
	
	public NBTHelper setTag(String key, NBTBase value) {
		this.nbt.setTag(key, value);
		return this;
	}
	
	public NBTHelper setTag(String key, NBTHelper value) {
		this.nbt.setTag(key, value.nbt);
		return this;
	}
	
	public NBTHelper setByte(String key, byte value) {
		this.nbt.setByte(key, value);
		return this;
	}
	
	public NBTHelper setByte(String key, int value) {
		return setByte(key, (byte) value);
	}
	
	public NBTHelper setShort(String key, short value) {
		this.nbt.setShort(key, value);
		return this;
	}
	
	public NBTHelper setShort(String key, int value) {
		return setShort(key, (short) value);
	}
	
	public NBTHelper setInteger(String key, int value) {
		this.nbt.setInteger(key, value);
		return this;
	}
	
	public NBTHelper setLong(String key, long value) {
		this.nbt.setLong(key, value);
		return this;
	}
	
	public NBTHelper setLong(String key, int value) {
		return setLong(key, (long) value);
	}
	
	public NBTHelper setFloat(String key, float value) {
		this.nbt.setFloat(key, value);
		return this;
	}
	
	public NBTHelper setFloat(String key, int value) {
		return setFloat(key, (float) value);
	}
	
	public NBTHelper setDouble(String key, double value) {
		this.nbt.setDouble(key, value);
		return this;
	}
	
	public NBTHelper setDouble(String key, int value) {
		return setDouble(key, (double) value);
	}
	
	public NBTHelper setString(String key, String value, boolean skipNull) {
		if (skipNull && value == null)
			return this;
		this.nbt.setString(key, value);
		return this;
	}
	
	public NBTHelper setString(String key, String value) {
		return setString(key, value, false);
	}
	
	public NBTHelper setByteArray(String key, byte[] value) {
		this.nbt.setByteArray(key, value);
		return this;
	}
	
	public NBTHelper setIntArray(String key, int[] value) {
		this.nbt.setIntArray(key, value);
		return this;
	}
	
	public NBTHelper setStringArray(String key, String[] value) {
		NBTTagList list = new NBTTagList();
		for (String s : value) {
			list.appendTag(new NBTTagString(s));
		}
		this.nbt.setTag(key, list);
	
		return this;
	}
	
	public NBTHelper setBoolean(String key, boolean value) {
		this.setByte(key, (byte) (value ? 1 : 0));
		return this;
	}
	
	public NBTHelper addAll(NBTTagCompound nbt) {
		if (nbt == null)
			return this;
		Set<String> keys = nbt.getKeySet();
		for (String key : keys) {
			Tag tagType = TagMap[nbt.getTagId(key)];
			switch (tagType) {
			case BYTE:
				setByte(key, nbt.getByte(key));
				break;
			case SHORT:
				setShort(key, nbt.getShort(key));
				break;
			case INT:
				setInteger(key, nbt.getInteger(key));
				break;
			case LONG:
				setLong(key, nbt.getLong(key));
				break;
			case FLOAT:
				setFloat(key, nbt.getFloat(key));
				break;
			case DOUBLE:
				setDouble(key, nbt.getDouble(key));
				break;
			case BYTE_ARRAY:
				setByteArray(key, nbt.getByteArray(key));
				break;
			case STRING:
				setString(key, nbt.getString(key));
				break;
			case LIST:
			case COMPOUND:
				setTag(key, nbt.getTag(key));
				break;
			case INT_ARRAY:
				setIntArray(key, nbt.getIntArray(key));
				break;
			default:
				break;
			}
		}
		return this;
	}
	
	public NBTHelper addAll(NBTHelper builder) {
		if (builder == null)
			return this;
		return addAll(builder.nbt);
	}
	*/
}
