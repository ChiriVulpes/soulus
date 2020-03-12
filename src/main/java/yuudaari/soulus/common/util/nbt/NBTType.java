package yuudaari.soulus.common.util.nbt;

import net.minecraft.nbt.NBTBase;

public enum NBTType {

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

	public boolean is (final int type) {
		return ordinal() == type;
	}

	public static NBTType get (final NBTBase nbt) {
		return values()[nbt.getId()];
	}

	public static NBTType get (final int type) {
		return values()[type];
	}
}
