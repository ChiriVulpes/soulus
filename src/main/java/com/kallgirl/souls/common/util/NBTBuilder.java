package com.kallgirl.souls.common.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

public class NBTBuilder {
	public NBTTagCompound nbt;

	public NBTBuilder() {
		this.nbt = new NBTTagCompound();
	}
	public NBTBuilder(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	public NBTBuilder setTag(String key, NBTBase value) {
		nbt.setTag(key, value);
		return this;
	}
	public NBTBuilder setTag(String key, NBTBuilder value) {
		nbt.setTag(key, value.nbt);
		return this;
	}

	public NBTBuilder setByte(String key, byte value) {
		nbt.setByte(key, value);
		return this;
	}
	public NBTBuilder setByte(String key, int value) {
		return setByte(key, (byte)value);
	}

	public NBTBuilder setShort(String key, short value) {
		nbt.setShort(key, value);
		return this;
	}
	public NBTBuilder setShort(String key, int value) {
		return setShort(key, (short)value);
	}

	public NBTBuilder setInteger(String key, int value) {
		nbt.setInteger(key, value);
		return this;
	}

	public NBTBuilder setLong(String key, long value) {
		nbt.setLong(key, value);
		return this;
	}
	public NBTBuilder setLong(String key, int value) {
		return setLong(key, (long)value);
	}

	public NBTBuilder setFloat(String key, float value) {
		nbt.setFloat(key, value);
		return this;
	}
	public NBTBuilder setFloat(String key, int value) {
		return setFloat(key, (float)value);
	}

	public NBTBuilder setDouble(String key, double value) {
		nbt.setDouble(key, value);
		return this;
	}
	public NBTBuilder setDouble(String key, int value) {
		return setDouble(key, (double)value);
	}

	public NBTBuilder setString(String key, String value, boolean skipNull) {
		if (skipNull && value == null) return this;
		nbt.setString(key, value);
		return this;
	}
	public NBTBuilder setString(String key, String value) {
		return setString(key, value, false);
	}

	public NBTBuilder setByteArray(String key, byte[] value) {
		nbt.setByteArray(key, value);
		return this;
	}

	public NBTBuilder setIntArray(String key, int[] value) {
		nbt.setIntArray(key, value);
		return this;
	}

	public NBTBuilder setBoolean(String key, boolean value) {
		this.setByte(key, (byte)(value ? 1 : 0));
		return this;
	}

	public NBTBuilder addAll(NBTTagCompound nbt) {
		if (nbt == null) return this;
		Set<String> keys = nbt.getKeySet();
		for (String key : keys) {
			byte tagId = nbt.getTagId(key);
			switch (tagId) {
				case 1: setByte(key, nbt.getByte(key)); break;
				case 2: setShort(key, nbt.getShort(key)); break;
				case 3: setInteger(key, nbt.getInteger(key)); break;
				case 4: setLong(key, nbt.getLong(key)); break;
				case 5: setFloat(key, nbt.getFloat(key)); break;
				case 6: setDouble(key, nbt.getDouble(key)); break;
				case 7: setByteArray(key, nbt.getByteArray(key)); break;
				case 8: setString(key, nbt.getString(key)); break;
				case 9: case 10: setTag(key, nbt.getTag(key)); break;
				case 11: setIntArray(key, nbt.getIntArray(key)); break;
			}
		}
		return this;
	}
	public NBTBuilder addAll(NBTBuilder builder) {
		if (builder == null) return this;
		return addAll(builder.nbt);
	}
}
