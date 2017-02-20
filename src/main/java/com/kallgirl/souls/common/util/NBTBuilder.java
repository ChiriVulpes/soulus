package com.kallgirl.souls.common.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTBuilder {
	public NBTTagCompound nbt = new NBTTagCompound();

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

	public NBTBuilder setShort(String key, short value) {
		nbt.setShort(key, value);
		return this;
	}

	public NBTBuilder setInteger(String key, int value) {
		nbt.setInteger(key, value);
		return this;
	}

	public NBTBuilder setLong(String key, long value) {
		nbt.setLong(key, value);
		return this;
	}

	public NBTBuilder setFloat(String key, float value) {
		nbt.setFloat(key, value);
		return this;
	}

	public NBTBuilder setDouble(String key, double value) {
		nbt.setDouble(key, value);
		return this;
	}

	public NBTBuilder setString(String key, String value) {
		nbt.setString(key, value);
		return this;
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
}
