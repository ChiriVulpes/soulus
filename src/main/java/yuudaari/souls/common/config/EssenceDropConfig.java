package yuudaari.souls.common.config;

import java.util.HashMap;
import java.util.Map;
import yuudaari.souls.common.util.BoneType;

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
		dropMapNormal.put("pig", new EssenceDropConfig(10));
		dropMapNormal.put("chicken", new EssenceDropConfig(10));
		dropMapNormal.put("cow", new EssenceDropConfig(8));
		dropMapNormal.put("sheep", new EssenceDropConfig(8));
		dropMapNormal.put("wolf", new EssenceDropConfig(6));
		dropMapNormal.put("polar_bear", new EssenceDropConfig(4));
		dropMapNormal.put("rabbit", new EssenceDropConfig(4));
		dropMapNormal.put("bat", new EssenceDropConfig(4));
		dropMapNormal.put("villager", new EssenceDropConfig(1));
		dropMapNormal.put("ocelot", new EssenceDropConfig(4));
		dropMapNormal.put("skeleton", new EssenceDropConfig(3));
		dropMapNormal.put("zombie", new EssenceDropConfig(2));
		dropMapNormal.put("horse", new EssenceDropConfig(1));

		// NETHER
		Map<String, EssenceDropConfig> dropMapNether = new HashMap<>();
		result.put(BoneType.NETHER, dropMapNether);

		dropMapNether.put("none", new EssenceDropConfig(10));
		dropMapNether.put("zombie_pigman", new EssenceDropConfig(8));
		dropMapNether.put("spider", new EssenceDropConfig(7));
		dropMapNether.put("cave_spider", new EssenceDropConfig(3));
		dropMapNether.put("blaze", new EssenceDropConfig(3));
		dropMapNether.put("wither_skeleton", new EssenceDropConfig(1));
		dropMapNether.put("witch", new EssenceDropConfig(1));
		dropMapNether.put("ghast", new EssenceDropConfig(1));

		// ENDER
		Map<String, EssenceDropConfig> dropMapEnder = new HashMap<>();
		result.put(BoneType.ENDER, dropMapEnder);

		dropMapEnder.put("none", new EssenceDropConfig(20));
		dropMapEnder.put("endermite", new EssenceDropConfig(6));
		dropMapEnder.put("creeper", new EssenceDropConfig(3));
		dropMapEnder.put("enderman", new EssenceDropConfig(2));
		dropMapEnder.put("shulker", new EssenceDropConfig(1));

		// SCALE
		Map<String, EssenceDropConfig> dropMapScale = new HashMap<>();
		result.put(BoneType.SCALE, dropMapScale);

		dropMapScale.put("none", new EssenceDropConfig(50));
		dropMapScale.put("squid", new EssenceDropConfig(60));
		dropMapScale.put("guardian", new EssenceDropConfig(10));
		dropMapScale.put("elder_guardian", new EssenceDropConfig(1));
		dropMapScale.put("silverfish", new EssenceDropConfig(5));

		// MISC
		Map<String, EssenceDropConfig> dropMapMisc = new HashMap<>();
		result.put(BoneType.MISC, dropMapMisc);

		dropMapMisc.put("slime", new EssenceDropConfig(0));
		dropMapMisc.put("magmacube", new EssenceDropConfig(0));
		dropMapMisc.put("mooshroom", new EssenceDropConfig(0));

		return result;
	}
}