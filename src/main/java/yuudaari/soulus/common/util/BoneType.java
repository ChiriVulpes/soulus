package yuudaari.soulus.common.util;

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
}
