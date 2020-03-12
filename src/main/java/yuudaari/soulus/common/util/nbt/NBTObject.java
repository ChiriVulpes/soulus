package yuudaari.soulus.common.util.nbt;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class NBTObject extends NBTHelper {

	public final NBTTagCompound nbt;

	public NBTObject () {
		nbt = new NBTTagCompound();
	}

	public NBTObject (final NBTTagCompound nbt) {
		this.nbt = nbt;
	}


	////////////////////////////////////
	// Has
	//

	public boolean has (final String key) {
		return nbt.hasKey(key);
	}

	public boolean has (final String key, final NBTType tagType) {
		return nbt.hasKey(key, tagType.ordinal());
	}

	public boolean hasByte (final String key) {
		return has(key, NBTType.BYTE);
	}

	public boolean hasShort (final String key) {
		return has(key, NBTType.SHORT);
	}

	public boolean hasInteger (final String key) {
		return has(key, NBTType.INT);
	}

	public boolean hasLong (final String key) {
		return has(key, NBTType.LONG);
	}

	public boolean hasFloat (final String key) {
		return has(key, NBTType.FLOAT);
	}

	public boolean hasDouble (final String key) {
		return has(key, NBTType.DOUBLE);
	}

	public boolean hasByteArray (final String key) {
		return has(key, NBTType.BYTE_ARRAY);
	}

	public boolean hasString (final String key) {
		return has(key, NBTType.STRING);
	}

	public boolean hasList (final String key) {
		return has(key, NBTType.LIST);
	}

	public boolean hasList (final String key, final NBTType type) {
		return has(key, NBTType.LIST) && getList(key).getListType() == type;
	}

	public boolean hasObject (final String key) {
		return has(key, NBTType.COMPOUND);
	}

	public boolean hasIntArray (final String key) {
		return has(key, NBTType.INT_ARRAY);
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

	public Stream<String> keyStream (final NBTType type) {
		return keyStream()
			.filter(key -> has(key, type));
	}

	public Stream<Map.Entry<String, NBTBase>> entryStream () {
		return getKeys().stream().map(key -> new AbstractMap.SimpleEntry<>(key, get(key)));
	}

	public Stream<Map.Entry<String, NBTBase>> entryStream (final NBTType type) {
		return entryStream()
			.filter(entry -> entry.getValue().getId() == type.ordinal());
	}

	public Stream<NBTBase> valueStream () {
		return getKeys().stream().map(this::get);
	}

	public Stream<NBTBase> valueStream (final NBTType type) {
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
		return has(key, NBTType.BYTE) ? nbt.getByte(key) : orElse;
	}

	public short getShort (final String key) {
		return nbt.getShort(key);
	}

	public short getShort (final String key, final short orElse) {
		return has(key, NBTType.SHORT) ? nbt.getShort(key) : orElse;
	}

	public int getInteger (final String key) {
		return nbt.getInteger(key);
	}

	public int getInteger (final String key, final int orElse) {
		return has(key, NBTType.INT) ? nbt.getInteger(key) : orElse;
	}

	public long getLong (String key) {
		return nbt.getLong(key);
	}

	public long getLong (final String key, final long orElse) {
		return has(key, NBTType.LONG) ? nbt.getLong(key) : orElse;
	}

	public float getFloat (String key) {
		return nbt.getFloat(key);
	}

	public float getFloat (final String key, final float orElse) {
		return has(key, NBTType.FLOAT) ? nbt.getFloat(key) : orElse;
	}

	public double getDouble (String key) {
		return nbt.getDouble(key);
	}

	public double getDouble (final String key, final double orElse) {
		return has(key, NBTType.DOUBLE) ? nbt.getDouble(key) : orElse;
	}

	public byte[] getByteArray (String key) {
		return nbt.getByteArray(key);
	}

	public byte[] getByteArray (final String key, final byte[] orElse) {
		return has(key, NBTType.BYTE_ARRAY) ? nbt.getByteArray(key) : orElse;
	}

	public String getString (String key) {
		return nbt.getString(key);
	}

	public String getString (final String key, final String orElse) {
		return has(key, NBTType.STRING) ? nbt.getString(key) : orElse;
	}

	public String[] getStringArray (String key) {
		if (!hasList(key, NBTType.STRING))
			return new String[0];

		return this.<NBTPrimitive<String>>getList(key)
			.stream()
			.map(NBTPrimitive::get)
			.toArray(String[]::new);
	}

	public String[] getStringArray (final String key, final String[] orElse) {
		return hasList(key, NBTType.STRING) ? getStringArray(key) : orElse;
	}

	public <T extends NBTHelper> NBTList<T> getList (final String key) {
		return new NBTList<T>((NBTTagList) nbt.getTag(key));
	}

	public <T extends NBTHelper> NBTList<T> getList (final String key, final NBTList<T> orElse) {
		return has(key, NBTType.LIST) ? getList(key) : orElse;
	}

	public <T extends NBTHelper> NBTList<T> getList (final String key, final int type, final NBTList<T> orElse) {
		final NBTList<T> result = has(key, NBTType.LIST) ? this.<T>getList(key) : orElse;
		return result.getListType().is(type) ? result : orElse;
	}

	public NBTObject getObject (final String key) {
		return new NBTObject(nbt.getCompoundTag(key));
	}

	public NBTObject getObject (final String key, final NBTObject orElse) {
		return has(key, NBTType.COMPOUND) ? new NBTObject(nbt.getCompoundTag(key)) : orElse;
	}

	public int[] getIntArray (final String key) {
		return nbt.getIntArray(key);
	}

	public int[] getIntArray (final String key, final int[] orElse) {
		return has(key, NBTType.INT_ARRAY) ? nbt.getIntArray(key) : orElse;
	}


	////////////////////////////////////
	// Set
	//

	public NBTObject setTag (final String key, final NBTBase value) {
		nbt.setTag(key, value);
		return this;
	}

	public NBTObject setObject (final String key, final NBTObject value) {
		nbt.setTag(key, value.nbt);
		return this;
	}

	public NBTObject setByte (final String key, final byte value) {
		nbt.setByte(key, value);
		return this;
	}

	public NBTObject setByte (final String key, final int value) {
		return setByte(key, (byte) value);
	}

	public NBTObject setShort (final String key, final short value) {
		nbt.setShort(key, value);
		return this;
	}

	public NBTObject setShort (final String key, final int value) {
		return setShort(key, (short) value);
	}

	public NBTObject setInteger (final String key, final int value) {
		nbt.setInteger(key, value);
		return this;
	}

	public NBTObject setLong (final String key, final long value) {
		nbt.setLong(key, value);
		return this;
	}

	public NBTObject setLong (final String key, final int value) {
		return setLong(key, (long) value);
	}

	public NBTObject setFloat (final String key, final float value) {
		nbt.setFloat(key, value);
		return this;
	}

	public NBTObject setFloat (final String key, final int value) {
		return setFloat(key, (float) value);
	}

	public NBTObject setDouble (final String key, final double value) {
		nbt.setDouble(key, value);
		return this;
	}

	public NBTObject setDouble (final String key, final int value) {
		return setDouble(key, (double) value);
	}

	public NBTObject setString (final String key, final String value) {
		nbt.setString(key, value);
		return this;
	}

	public NBTObject setByteArray (final String key, final byte[] value) {
		nbt.setByteArray(key, value);
		return this;
	}

	public NBTObject setIntArray (final String key, final int[] value) {
		nbt.setIntArray(key, value);
		return this;
	}

	public NBTObject setStringArray (final String key, final String[] value) {
		final NBTTagList list = new NBTTagList();
		for (final String s : value)
			list.appendTag(new NBTTagString(s));
		nbt.setTag(key, list);

		return this;
	}

	public NBTObject setBoolean (final String key, final boolean value) {
		setByte(key, (byte) (value ? 1 : 0));
		return this;
	}


	////////////////////////////////////
	// Remove
	//

	public NBTObject remove (final String key) {
		nbt.removeTag(key);
		return this;
	}

	public NBTObject remove (final String key, final NBTType type) {
		if (has(key, type))
			nbt.removeTag(key);
		return this;
	}

	public NBTObject removeByte (final String key) {
		return remove(key, NBTType.BYTE);
	}

	public NBTObject removeShort (final String key) {
		return remove(key, NBTType.SHORT);
	}

	public NBTObject removeInteger (final String key) {
		return remove(key, NBTType.INT);
	}

	public NBTObject removeLong (final String key) {
		return remove(key, NBTType.LONG);
	}

	public NBTObject removeFloat (final String key) {
		return remove(key, NBTType.FLOAT);
	}

	public NBTObject removeDouble (final String key) {
		return remove(key, NBTType.DOUBLE);
	}

	public NBTObject removeByteArray (final String key) {
		return remove(key, NBTType.BYTE_ARRAY);
	}

	public NBTObject removeString (final String key) {
		return remove(key, NBTType.STRING);
	}

	public NBTObject removeList (final String key) {
		return remove(key, NBTType.LIST);
	}

	public NBTObject removeList (final String key, final NBTType type) {
		if (has(key, NBTType.LIST) && getList(key).is(type))
			remove(key);
		return this;
	}

	public NBTObject removeObject (final String key) {
		return remove(key, NBTType.COMPOUND);
	}

	public NBTObject removeIntArray (final String key) {
		return remove(key, NBTType.INT_ARRAY);
	}


	////////////////////////////////////
	// Compute
	//

	public byte computeByte (final String key, final Function<String, Byte> computer) {
		if (has(key, NBTType.BYTE))
			return getByte(key);

		final byte result = computer.apply(key);
		setByte(key, result);
		return result;
	}

	public short computeShort (final String key, final Function<String, Short> computer) {
		if (has(key, NBTType.SHORT))
			return getShort(key);

		final short result = computer.apply(key);
		setShort(key, result);
		return result;
	}

	public int computeINT (final String key, final Function<String, Integer> computer) {
		if (has(key, NBTType.INT))
			return getInteger(key);

		final int result = computer.apply(key);
		setInteger(key, result);
		return result;
	}

	public long computeLong (final String key, final Function<String, Long> computer) {
		if (has(key, NBTType.LONG))
			return getLong(key);

		final long result = computer.apply(key);
		setLong(key, result);
		return result;
	}

	public float computeFloat (final String key, final Function<String, Float> computer) {
		if (has(key, NBTType.FLOAT))
			return getFloat(key);

		final float result = computer.apply(key);
		setFloat(key, result);
		return result;
	}

	public double computeDouble (final String key, final Function<String, Double> computer) {
		if (has(key, NBTType.DOUBLE))
			return getDouble(key);

		final double result = computer.apply(key);
		setDouble(key, result);
		return result;
	}

	public byte[] computeByteArray (final String key, final Function<String, Byte[]> computer) {
		if (has(key, NBTType.BYTE_ARRAY))
			return getByteArray(key);

		final byte[] result = ArrayUtils.toPrimitive(computer.apply(key));
		setByteArray(key, result);
		return result;
	}

	public String computeString (final String key, final Function<String, String> computer) {
		if (has(key, NBTType.STRING))
			return getString(key);

		final String result = computer.apply(key);
		setString(key, result);
		return result;
	}

	public String[] computeStringArray (final String key, final Function<String, String[]> computer) {
		if (hasList(key, NBTType.STRING))
			return this.<NBTPrimitive<String>>getList(key)
				.streamPrimitives()
				.toArray(String[]::new);

		final String[] result = computer.apply(key);
		setStringArray(key, result);
		return result;
	}

	public <T extends NBTHelper> NBTList<T> computeList (final String key, final Function<String, NBTList<T>> computer) {
		if (has(key, NBTType.LIST))
			return this.<T>getList(key);

		final NBTList<T> result = computer.apply(key);
		setTag(key, result.nbt);
		return result;
	}

	public NBTObject computeObject (final String key, final Function<String, NBTObject> computer) {
		if (has(key, NBTType.COMPOUND))
			return getObject(key);

		final NBTObject result = computer.apply(key);
		setObject(key, result);
		return result;
	}

	public int[] computeIntArray (final String key, final Function<String, Integer[]> computer) {
		if (has(key, NBTType.INT_ARRAY))
			return getIntArray(key);

		final int[] result = ArrayUtils.toPrimitive(computer.apply(key));
		setIntArray(key, result);
		return result;
	}



	////////////////////////////////////
	// Add all
	//

	public NBTObject addAll (final NBTTagCompound nbt) {
		if (nbt == null)
			return this;

		for (final String key : nbt.getKeySet()) {
			final NBTType tagType = NBTType.get(nbt.getTagId(key));

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

	public NBTObject addAll (final NBTObject builder) {
		if (builder == null) return this;
		return addAll(builder.nbt);
	}
}
