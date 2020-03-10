package yuudaari.soulus.common.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class NBTHelper {

	public NBTTagCompound nbt;

	public static enum Tag {
		END,
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		BYTE_ARRAY,
		STRING,
		LIST,
		COMPOUND,
		INT_ARRAY;
	}

	public NBTHelper () {
		nbt = new NBTTagCompound();
	}

	public NBTHelper (final NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	public NBTHelper (final ItemStack stack) {
		this(stack, true);
	}

	public NBTHelper (final ItemStack stack, final boolean createIfNotExist) {
		nbt = stack.getTagCompound();
		if (nbt == null && createIfNotExist)
			stack.setTagCompound(nbt = new NBTTagCompound());
	}


	////////////////////////////////////
	// Has
	//

	public boolean has (final String key) {
		return nbt.hasKey(key);
	}

	public boolean has (final String key, final Tag tagType) {
		return nbt.hasKey(key, tagType.ordinal());
	}

	public boolean hasByte (final String key) {
		return has(key, Tag.BYTE);
	}

	public boolean hasShort (final String key) {
		return has(key, Tag.SHORT);
	}

	public boolean hasInteger (final String key) {
		return has(key, Tag.INT);
	}

	public boolean hasLong (final String key) {
		return has(key, Tag.LONG);
	}

	public boolean hasFloat (final String key) {
		return has(key, Tag.FLOAT);
	}

	public boolean hasDouble (final String key) {
		return has(key, Tag.DOUBLE);
	}

	public boolean hasByteArray (final String key) {
		return has(key, Tag.BYTE_ARRAY);
	}

	public boolean hasString (final String key) {
		return has(key, Tag.STRING);
	}

	public boolean hasList (final String key) {
		return has(key, Tag.LIST);
	}

	public boolean hasList (final String key, final Tag type) {
		return has(key, Tag.LIST) && getList(key).getTagType() == type.ordinal();
	}

	public boolean hasObject (final String key) {
		return has(key, Tag.COMPOUND);
	}

	public boolean hasIntArray (final String key) {
		return has(key, Tag.INT_ARRAY);
	}


	////////////////////////////////////
	// Stream
	//

	public Set<String> getKeys () {
		return nbt.getKeySet();
	}

	public Stream<String> keyStream () {
		return getKeys().stream();
	}

	public Stream<String> keyStream (final Tag type) {
		return keyStream()
			.filter(key -> has(key, type));
	}

	public Stream<Map.Entry<String, NBTBase>> entryStream () {
		return getKeys().stream().map(key -> new AbstractMap.SimpleEntry<>(key, get(key)));
	}

	public Stream<Map.Entry<String, NBTBase>> entryStream (final Tag type) {
		return entryStream()
			.filter(entry -> entry.getValue().getId() == type.ordinal());
	}

	public Stream<NBTBase> valueStream () {
		return getKeys().stream().map(this::get);
	}

	public Stream<NBTBase> valueStream (final Tag type) {
		return valueStream()
			.filter(value -> value.getId() == type.ordinal());
	}


	////////////////////////////////////
	// Get
	//

	public NBTBase get (final String key) {
		return nbt.getTag(key);
	}

	public byte getByte (final String key) {
		return nbt.getByte(key);
	}

	public byte getByte (final String key, final byte orElse) {
		return has(key, Tag.BYTE) ? nbt.getByte(key) : orElse;
	}

	public short getShort (final String key) {
		return nbt.getShort(key);
	}

	public short getShort (final String key, final short orElse) {
		return has(key, Tag.SHORT) ? nbt.getShort(key) : orElse;
	}

	public int getInteger (final String key) {
		return nbt.getInteger(key);
	}

	public int getInteger (final String key, final int orElse) {
		return has(key, Tag.INT) ? nbt.getInteger(key) : orElse;
	}

	public long getLong (String key) {
		return nbt.getLong(key);
	}

	public long getLong (final String key, final long orElse) {
		return has(key, Tag.LONG) ? nbt.getLong(key) : orElse;
	}

	public float getFloat (String key) {
		return nbt.getFloat(key);
	}

	public float getFloat (final String key, final float orElse) {
		return has(key, Tag.FLOAT) ? nbt.getFloat(key) : orElse;
	}

	public double getDouble (String key) {
		return nbt.getDouble(key);
	}

	public double getDouble (final String key, final double orElse) {
		return has(key, Tag.DOUBLE) ? nbt.getDouble(key) : orElse;
	}

	public byte[] getByteArray (String key) {
		return nbt.getByteArray(key);
	}

	public byte[] getByteArray (final String key, final byte[] orElse) {
		return has(key, Tag.BYTE_ARRAY) ? nbt.getByteArray(key) : orElse;
	}

	public String getString (String key) {
		return nbt.getString(key);
	}

	public String getString (final String key, final String orElse) {
		return has(key, Tag.STRING) ? nbt.getString(key) : orElse;
	}

	public String[] getStringArray (String key) {
		if (!hasList(key, Tag.STRING))
			return new String[0];

		return StreamSupport.stream(getList(key).spliterator(), false)
			.map(str -> ((NBTTagString) str).getString())
			.toArray(String[]::new);
	}

	public String[] getStringArray (final String key, final String[] orElse) {
		return hasList(key, Tag.STRING) ? getStringArray(key) : orElse;
	}

	public NBTTagList getList (final String key) {
		return (NBTTagList) nbt.getTag(key);
	}

	public NBTTagList getList (final String key, final NBTTagList orElse) {
		return has(key, Tag.LIST) ? getList(key) : orElse;
	}

	public NBTTagList getList (final String key, final int type, final NBTTagList orElse) {
		final NBTTagList result = has(key, Tag.LIST) ? getList(key) : orElse;
		return result.getTagType() == type ? result : orElse;
	}

	public NBTHelper getObject (final String key) {
		return new NBTHelper(nbt.getCompoundTag(key));
	}

	public NBTHelper getObject (final String key, final NBTHelper orElse) {
		return has(key, Tag.COMPOUND) ? new NBTHelper(nbt.getCompoundTag(key)) : orElse;
	}

	public int[] getIntArray (final String key) {
		return nbt.getIntArray(key);
	}

	public int[] getIntArray (final String key, final int[] orElse) {
		return has(key, Tag.INT_ARRAY) ? nbt.getIntArray(key) : orElse;
	}


	////////////////////////////////////
	// Set
	//

	public NBTHelper setTag (final String key, final NBTBase value) {
		nbt.setTag(key, value);
		return this;
	}

	public NBTHelper setObject (final String key, final NBTHelper value) {
		nbt.setTag(key, value.nbt);
		return this;
	}

	public NBTHelper setByte (final String key, final byte value) {
		nbt.setByte(key, value);
		return this;
	}

	public NBTHelper setByte (final String key, final int value) {
		return setByte(key, (byte) value);
	}

	public NBTHelper setShort (final String key, final short value) {
		nbt.setShort(key, value);
		return this;
	}

	public NBTHelper setShort (final String key, final int value) {
		return setShort(key, (short) value);
	}

	public NBTHelper setInteger (final String key, final int value) {
		nbt.setInteger(key, value);
		return this;
	}

	public NBTHelper setLong (final String key, final long value) {
		nbt.setLong(key, value);
		return this;
	}

	public NBTHelper setLong (final String key, final int value) {
		return setLong(key, (long) value);
	}

	public NBTHelper setFloat (final String key, final float value) {
		nbt.setFloat(key, value);
		return this;
	}

	public NBTHelper setFloat (final String key, final int value) {
		return setFloat(key, (float) value);
	}

	public NBTHelper setDouble (final String key, final double value) {
		nbt.setDouble(key, value);
		return this;
	}

	public NBTHelper setDouble (final String key, final int value) {
		return setDouble(key, (double) value);
	}

	public NBTHelper setString (final String key, final String value) {
		nbt.setString(key, value);
		return this;
	}

	public NBTHelper setByteArray (final String key, final byte[] value) {
		nbt.setByteArray(key, value);
		return this;
	}

	public NBTHelper setIntArray (final String key, final int[] value) {
		nbt.setIntArray(key, value);
		return this;
	}

	public NBTHelper setStringArray (final String key, final String[] value) {
		final NBTTagList list = new NBTTagList();
		for (final String s : value)
			list.appendTag(new NBTTagString(s));
		nbt.setTag(key, list);

		return this;
	}

	public NBTHelper setBoolean (final String key, final boolean value) {
		setByte(key, (byte) (value ? 1 : 0));
		return this;
	}


	////////////////////////////////////
	// Remove
	//

	public NBTHelper remove (final String key) {
		nbt.removeTag(key);
		return this;
	}

	public NBTHelper remove (final String key, final Tag type) {
		if (has(key, type))
			nbt.removeTag(key);
		return this;
	}

	public NBTHelper removeByte (final String key) {
		return remove(key, Tag.BYTE);
	}

	public NBTHelper removeShort (final String key) {
		return remove(key, Tag.SHORT);
	}

	public NBTHelper removeInteger (final String key) {
		return remove(key, Tag.INT);
	}

	public NBTHelper removeLong (final String key) {
		return remove(key, Tag.LONG);
	}

	public NBTHelper removeFloat (final String key) {
		return remove(key, Tag.FLOAT);
	}

	public NBTHelper removeDouble (final String key) {
		return remove(key, Tag.DOUBLE);
	}

	public NBTHelper removeByteArray (final String key) {
		return remove(key, Tag.BYTE_ARRAY);
	}

	public NBTHelper removeString (final String key) {
		return remove(key, Tag.STRING);
	}

	public NBTHelper removeList (final String key) {
		return remove(key, Tag.LIST);
	}

	public NBTHelper removeList (final String key, final Tag type) {
		if (has(key, Tag.LIST) && getList(key).getTagType() == type.ordinal())
			remove(key);
		return this;
	}

	public NBTHelper removeObject (final String key) {
		return remove(key, Tag.COMPOUND);
	}

	public NBTHelper removeIntArray (final String key) {
		return remove(key, Tag.INT_ARRAY);
	}


	////////////////////////////////////
	// Compute
	//

	public byte computeByte (final String key, final Function<String, Byte> computer) {
		if (has(key, Tag.BYTE))
			return getByte(key);

		final byte result = computer.apply(key);
		setByte(key, result);
		return result;
	}

	public short computeShort (final String key, final Function<String, Short> computer) {
		if (has(key, Tag.SHORT))
			return getShort(key);

		final short result = computer.apply(key);
		setShort(key, result);
		return result;
	}

	public int computeINT (final String key, final Function<String, Integer> computer) {
		if (has(key, Tag.INT))
			return getInteger(key);

		final int result = computer.apply(key);
		setInteger(key, result);
		return result;
	}

	public long computeLong (final String key, final Function<String, Long> computer) {
		if (has(key, Tag.LONG))
			return getLong(key);

		final long result = computer.apply(key);
		setLong(key, result);
		return result;
	}

	public float computeFloat (final String key, final Function<String, Float> computer) {
		if (has(key, Tag.FLOAT))
			return getFloat(key);

		final float result = computer.apply(key);
		setFloat(key, result);
		return result;
	}

	public double computeDouble (final String key, final Function<String, Double> computer) {
		if (has(key, Tag.DOUBLE))
			return getDouble(key);

		final double result = computer.apply(key);
		setDouble(key, result);
		return result;
	}

	public byte[] computeByteArray (final String key, final Function<String, Byte[]> computer) {
		if (has(key, Tag.BYTE_ARRAY))
			return getByteArray(key);

		final byte[] result = ArrayUtils.toPrimitive(computer.apply(key));
		setByteArray(key, result);
		return result;
	}

	public String computeString (final String key, final Function<String, String> computer) {
		if (has(key, Tag.STRING))
			return getString(key);

		final String result = computer.apply(key);
		setString(key, result);
		return result;
	}

	public String[] computeStringArray (final String key, final Function<String, String[]> computer) {
		if (hasList(key, Tag.STRING))
			return StreamSupport.stream(getList(key).spliterator(), false)
				.map(str -> ((NBTTagString) str).getString())
				.toArray(String[]::new);

		final String[] result = computer.apply(key);
		setStringArray(key, result);
		return result;
	}

	public NBTTagList computeList (final String key, final Function<String, NBTTagList> computer) {
		if (has(key, Tag.LIST))
			return getList(key);

		final NBTTagList result = computer.apply(key);
		setTag(key, result);
		return result;
	}

	public NBTHelper computeObject (final String key, final Function<String, NBTHelper> computer) {
		if (has(key, Tag.COMPOUND))
			return getObject(key);

		final NBTHelper result = computer.apply(key);
		setObject(key, result);
		return result;
	}

	public int[] computeIntArray (final String key, final Function<String, Integer[]> computer) {
		if (has(key, Tag.INT_ARRAY))
			return getIntArray(key);

		final int[] result = ArrayUtils.toPrimitive(computer.apply(key));
		setIntArray(key, result);
		return result;
	}



	////////////////////////////////////
	// Add all
	//

	public NBTHelper addAll (final NBTTagCompound nbt) {
		if (nbt == null)
			return this;

		for (final String key : nbt.getKeySet()) {
			final Tag tagType = Tag.values()[nbt.getTagId(key)];

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

	public NBTHelper addAll (final NBTHelper builder) {
		if (builder == null) return this;
		return addAll(builder.nbt);
	}
}
