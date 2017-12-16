package yuudaari.soulus.common.config;

import java.util.HashMap;
import java.util.Map;
import yuudaari.soulus.common.util.BoneType;

public class EssenceDropConfig {
	public Double dropChance;

	public EssenceDropConfig() {
	}

	public EssenceDropConfig(double dropChance) {
		this.dropChance = dropChance;
	}

	public static Serializer<EssenceDropConfig> serializer = new Serializer<>(EssenceDropConfig.class, "dropChance");

	public static Map<BoneType, Map<String, EssenceDropConfig>> getDefaultDropMap() {

		Map<BoneType, Map<String, EssenceDropConfig>> result = new HashMap<>();

		// NORMAL
		Map<String, EssenceDropConfig> dropMapNormal = new HashMap<>();
		result.put(BoneType.NORMAL, dropMapNormal);

		dropMapNormal.put("none", new EssenceDropConfig(5));
		dropMapNormal.put("bat", new EssenceDropConfig(1));
		dropMapNormal.put("chicken", new EssenceDropConfig(8));
		dropMapNormal.put("cow", new EssenceDropConfig(8));
		dropMapNormal.put("pig", new EssenceDropConfig(10));
		dropMapNormal.put("rabbit", new EssenceDropConfig(4));
		dropMapNormal.put("sheep", new EssenceDropConfig(6));
		dropMapNormal.put("skeleton", new EssenceDropConfig(3));
		dropMapNormal.put("spider", new EssenceDropConfig(5));
		dropMapNormal.put("villager", new EssenceDropConfig(1));

		// DRY
		Map<String, EssenceDropConfig> dropMapDry = new HashMap<>();
		result.put(BoneType.DRY, dropMapDry);

		dropMapDry.put("none", new EssenceDropConfig(30));
		dropMapDry.put("cave_spider", new EssenceDropConfig(1));
		dropMapDry.put("horse", new EssenceDropConfig(1));
		dropMapDry.put("husk", new EssenceDropConfig(5));
		dropMapDry.put("llama", new EssenceDropConfig(10));

		// FUNGAL
		Map<String, EssenceDropConfig> dropMapFungal = new HashMap<>();
		result.put(BoneType.FUNGAL, dropMapFungal);

		dropMapFungal.put("none", new EssenceDropConfig(20));
		dropMapFungal.put("mooshroom", new EssenceDropConfig(1));
		dropMapFungal.put("ocelot", new EssenceDropConfig(10));
		dropMapFungal.put("parrot", new EssenceDropConfig(10));
		dropMapFungal.put("vindication_illager", new EssenceDropConfig(5));
		dropMapFungal.put("witch", new EssenceDropConfig(5));
		dropMapFungal.put("zombie", new EssenceDropConfig(20));

		// FROZEN
		Map<String, EssenceDropConfig> dropMapFrozen = new HashMap<>();
		result.put(BoneType.FROZEN, dropMapFrozen);

		dropMapFrozen.put("none", new EssenceDropConfig(10));
		dropMapFrozen.put("polar_bear", new EssenceDropConfig(10));
		dropMapFrozen.put("snowman", new EssenceDropConfig(1));
		dropMapFrozen.put("stray", new EssenceDropConfig(1));
		dropMapFrozen.put("wolf", new EssenceDropConfig(5));

		// NETHER
		Map<String, EssenceDropConfig> dropMapNether = new HashMap<>();
		result.put(BoneType.NETHER, dropMapNether);

		dropMapNether.put("none", new EssenceDropConfig(10));
		dropMapNether.put("blaze", new EssenceDropConfig(3));
		dropMapNether.put("ghast", new EssenceDropConfig(1));
		dropMapNether.put("wither_skeleton", new EssenceDropConfig(1));
		dropMapNether.put("zombie_pigman", new EssenceDropConfig(20));

		// SCALE
		Map<String, EssenceDropConfig> dropMapScale = new HashMap<>();
		result.put(BoneType.SCALE, dropMapScale);

		dropMapScale.put("none", new EssenceDropConfig(10));
		dropMapScale.put("guardian", new EssenceDropConfig(1));
		dropMapScale.put("silverfish", new EssenceDropConfig(1));
		dropMapScale.put("squid", new EssenceDropConfig(20));

		// ENDER
		Map<String, EssenceDropConfig> dropMapEnder = new HashMap<>();
		result.put(BoneType.ENDER, dropMapEnder);

		dropMapEnder.put("none", new EssenceDropConfig(20));
		dropMapEnder.put("creeper", new EssenceDropConfig(10));
		dropMapEnder.put("enderman", new EssenceDropConfig(2));
		dropMapEnder.put("endermite", new EssenceDropConfig(15));
		dropMapEnder.put("shulker", new EssenceDropConfig(1));

		return result;
	}
}