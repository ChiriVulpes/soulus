package yuudaari.soulus.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.util.serializer.FieldSerializer;

public enum BoneType {
	NORMAL,
	DRY,
	FUNGAL,
	FROZEN,
	SCALE,
	NETHER,
	ENDER;

	public static BoneType getBoneType (String type) {
		for (BoneType boneType : BoneType.values()) {
			if (boneType.toString().equalsIgnoreCase(type))
				return boneType;
		}
		return null;
	}

	public static String getString (BoneType type) {
		return type.toString().toLowerCase();
	}

	public static class Serializer extends FieldSerializer<BoneType> {

		@Override
		public JsonElement serialize (Class<?> objectType, BoneType type) {
			return new JsonPrimitive(BoneType.getString(type));
		}

		@Override
		public BoneType deserialize (Class<?> requestedType, JsonElement element) {
			return BoneType.getBoneType(element.getAsString());
		}
	}
}
