package yuudaari.soulus.common.block;

import java.util.Arrays;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.IStringSerializable;
import yuudaari.soulus.common.util.serializer.FieldSerializer;

public enum EndersteelType implements IStringSerializable {
	NORMAL (0, "normal"),
	WOOD (1, "wood"),
	STONE (2, "stone"),
	END_STONE (3, "end_stone"),
	BLAZE (4, "blaze");

	private final int meta;
	private final String name;

	EndersteelType (int meta, String name) {
		this.meta = meta;
		this.name = name;
	}

	public int getMeta () {
		return meta;
	}

	@Override
	public String getName () {
		return name;
	}

	public static EndersteelType byMetadata (int meta) {
		return meta < 0 || meta >= values().length ? EndersteelType.NORMAL : values()[meta];
	}

	public static EndersteelType byName (String name) {
		Optional<EndersteelType> endersteelType = Arrays.asList(EndersteelType.values())
			.stream()
			.filter(type -> type.getName().equalsIgnoreCase(name))
			.findFirst();
		return endersteelType.isPresent() ? endersteelType.get() : EndersteelType.NORMAL;
	}

	public static class Serializer extends FieldSerializer<EndersteelType> {

		@Override
		public JsonElement serialize (Class<?> objectType, EndersteelType type) {
			return new JsonPrimitive(type.name);
		}

		@Override
		public EndersteelType deserialize (Class<?> requestedType, JsonElement element) {
			return EndersteelType.byName(element.getAsString());
		}
	}
}
