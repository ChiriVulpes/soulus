package yuudaari.soulus.common.config_old;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.ModPotionEffect;

public class PotionEffectSerializer extends Serializer<ModPotionEffect[]> {

	public static final PotionEffectSerializer INSTANCE = new PotionEffectSerializer();

	@Override
	public JsonElement serialize (Object from) {
		ModPotionEffect[] effects = (ModPotionEffect[]) from;

		JsonArray result = new JsonArray();

		for (ModPotionEffect effect : effects)
			result.add(ModPotionEffect.serialize(effect));

		return result;
	}

	@Override
	public Object deserialize (JsonElement from, Object current) {
		if (from == null || !from.isJsonArray()) {
			Logger.warn("Must be an array of effects");
			return current;
		}

		JsonArray effects = from.getAsJsonArray();

		List<ModPotionEffect> result = new ArrayList<>();

		for (JsonElement effect : effects) {
			ModPotionEffect effectResult = (ModPotionEffect) ModPotionEffect.deserialize(effect, null);

			if (effectResult != null)
				result.add(effectResult);
		}

		return result.toArray(new ModPotionEffect[result.size()]);
	}
}
