package yuudaari.soulus.common.util.nbt;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.nbt.NBTTagList;

public class NBTList<T extends NBTHelper> extends NBTHelper {

	public final NBTTagList nbt;

	public NBTList () {
		nbt = new NBTTagList();
	}

	public NBTList (final NBTTagList nbt) {
		this.nbt = nbt;
	}

	public NBTType getListType () {
		return NBTType.values()[nbt.getTagType()];
	}

	public boolean is (final NBTType type) {
		return getListType() == type;
	}

	@SuppressWarnings("unchecked")
	public T get (final int index) {
		return (T) NBTHelper.get(nbt.get(index));
	}

	@SuppressWarnings("unchecked")
	public Stream<T> stream () {
		return (Stream<T>) StreamSupport.stream(nbt.spliterator(), false)
			.map(NBTHelper::get);
	}

	public Stream<T> streamPrimitives () {
		return StreamSupport.stream(nbt.spliterator(), false)
			.map(NBTHelper::get)
			.map(nbt -> nbt.<T>getAsPrimitive())
			.map(NBTPrimitive::get);
	}
}
