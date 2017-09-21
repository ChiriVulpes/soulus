package yuudaari.souls.common.config;

import java.util.HashMap;
import java.util.Map;
import yuudaari.souls.common.util.BoneType;

public class SoulConfig {
	public Integer quantity = 16;
	public ColourConfig colourInfo = null;

	public SoulConfig() {
	}

	public SoulConfig(int quantity) {
		this.quantity = quantity;
	}

	public static Serializer<SoulConfig> serializer = new Serializer<>(SoulConfig.class, "quantity");

	public static Map<String, SoulConfig> getDefaultSoulMap() {
		Map<String, SoulConfig> result = new HashMap<>();

		// default them all
		Map<BoneType, Map<String, EssenceDropConfig>> defaultDropMap = EssenceDropConfig.getDefaultDropMap();
		for (Map.Entry<BoneType, Map<String, EssenceDropConfig>> entry : defaultDropMap.entrySet()) {
			for (Map.Entry<String, EssenceDropConfig> soulEntry : entry.getValue().entrySet()) {
				result.put(soulEntry.getKey(), new SoulConfig(16));
			}
		}

		// explicitly change a few

		return result;
	}
}