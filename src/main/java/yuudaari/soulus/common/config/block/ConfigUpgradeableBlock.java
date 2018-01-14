package yuudaari.soulus.common.config.block;

import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlockTileEntity;
import yuudaari.soulus.common.block.upgradeable_block.UpgradeableBlock.IUpgrade;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.ClassSerializationEventHandlers.DeserializationEventHandler;
import yuudaari.soulus.common.util.serializer.ClassSerializationEventHandlers.SerializationEventHandler;

public abstract class ConfigUpgradeableBlock<T extends UpgradeableBlock<? extends UpgradeableBlockTileEntity>> {

	protected abstract T getBlock ();

	@SerializationEventHandler
	public static void onSerialization (final Object instance, final JsonObject object) {
		JsonObject result = new JsonObject();

		@SuppressWarnings("rawtypes")
		ConfigUpgradeableBlock config = (ConfigUpgradeableBlock) instance;

		for (IUpgrade upgrade : config.getBlock().getUpgrades()) {
			if (!upgrade.canOverrideMaxQuantity()) continue;

			String key = upgrade.getName().toLowerCase();
			result.addProperty(key, upgrade.getMaxQuantity());
		}

		object.add("max_upgrades", result);
	}

	@DeserializationEventHandler
	public static void onDeserialization (final Object instance, final JsonObject element) {
		JsonElement from = element.get("max_upgrades");

		if (from == null || !from.isJsonObject()) {
			Logger.warn("Max upgrades must be an object");
			return;
		}

		@SuppressWarnings("rawtypes")
		ConfigUpgradeableBlock config = (ConfigUpgradeableBlock) instance;

		JsonObject upgrades = from.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : upgrades.entrySet()) {
			JsonElement val = entry.getValue();

			if (val == null || !val.isJsonPrimitive() || !val.getAsJsonPrimitive().isNumber()) {
				Logger.warn("Upgrade maximum must be an number");
				continue;
			}

			String key = entry.getKey();
			IUpgrade upgrade = null;
			for (IUpgrade checkUpgrade : config.getBlock().getUpgrades()) {
				if (key.equalsIgnoreCase(checkUpgrade.getName())) {
					if (checkUpgrade.canOverrideMaxQuantity())
						upgrade = checkUpgrade;
					break;
				}
			}
			if (upgrade == null) {
				Logger.warn("Upgrade type '" + key + "' is invalid");
				continue;
			}

			upgrade.setMaxQuantity(val.getAsInt());
		}
	}
}
