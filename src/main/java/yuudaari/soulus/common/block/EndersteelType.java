package yuudaari.soulus.common.block;

import java.util.Comparator;
import java.util.stream.Stream;

import net.minecraft.util.IStringSerializable;

public enum EndersteelType implements IStringSerializable {
	NORMAL(0, "normal"), WOOD(1, "wood"), STONE(2, "stone"), END_STONE(3, "end_stone"), BLAZE(4, "blaze");

	private static final EndersteelType[] META_LOOKUP = Stream.of(values())
			.sorted(Comparator.comparing(EndersteelType::getMeta)).toArray(EndersteelType[]::new);

	private final int meta;
	private final String name;

	EndersteelType(int meta, String name) {
		this.meta = meta;
		this.name = name;
	}

	public int getMeta() {
		return meta;
	}

	@Override
	public String getName() {
		return name;
	}

	public static EndersteelType byMetadata(int meta) {
		if (meta < 0 || meta >= META_LOOKUP.length) {
			meta = 0;
		}

		return META_LOOKUP[meta];
	}

	public static String[] getNames() {
		return Stream.of(META_LOOKUP).map(EndersteelType::getName).toArray(String[]::new);
	}
}