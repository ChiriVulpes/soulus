package yuudaari.souls.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.souls.common.config.ManualSerializer;

public class ModPotionEffect extends PotionEffect {
	public static ManualSerializer serializer = new ManualSerializer(ModPotionEffect::serialize,
			ModPotionEffect::deserialize);

	private final String effect;
	private final int duration;

	public ModPotionEffect(String which, int duration) {
		super(ForgeRegistries.POTIONS.getValue(new ResourceLocation(which)), duration);
		this.effect = which;
		this.duration = duration;
	}

	public static JsonElement serialize(Object from) {
		ModPotionEffect effect = (ModPotionEffect) from;

		JsonObject result = new JsonObject();

		result.addProperty("effect", effect.effect);
		result.addProperty("duration", effect.duration);

		return result;
	}

	public static Object deserialize(JsonElement from, Object current) {
		if (from == null || !from.isJsonObject()) {
			Logger.warn("Must be a json object");
			return current;
		}

		JsonObject effectElement = (JsonObject) from;

		JsonElement effect = effectElement.get("effect");
		if (effect == null || !effect.isJsonPrimitive() || !effect.getAsJsonPrimitive().isString()) {
			Logger.warn("Must have property 'effect' set to a string");
			return current;
		}

		JsonElement duration = effectElement.get("duration");
		if (duration == null || !duration.isJsonPrimitive() || !duration.getAsJsonPrimitive().isNumber()) {
			Logger.warn("Must have property 'duration' set to a number");
			return current;
		}

		return new ModPotionEffect(effect.getAsString(), duration.getAsInt());
	}
}