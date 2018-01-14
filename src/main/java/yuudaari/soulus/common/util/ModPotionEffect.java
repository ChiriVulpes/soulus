package yuudaari.soulus.common.util;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.soulus.common.util.serializer.ClassSerializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable(ModPotionEffect.Serializer.class)
public class ModPotionEffect extends PotionEffect {

	public static class Serializer extends ClassSerializer<ModPotionEffect> {

		@Override
		@Nullable
		public ModPotionEffect instantiate (final Class<?> cls) {
			return null;
		}

		@Override
		public void serialize (ModPotionEffect effect, JsonObject object) {
			object.addProperty("effect", effect.effect);
			object.addProperty("duration", effect.duration);
		}

		@Override
		@Nullable
		public ModPotionEffect deserialize (final ModPotionEffect _null, final JsonElement element) {
			if (element == null || !element.isJsonObject()) {
				Logger.warn("Must be a json object");
				return null;
			}

			JsonObject effectJson = (JsonObject) element;

			JsonElement effect = effectJson.get("effect");
			if (effect == null || !effect.isJsonPrimitive() || !effect.getAsJsonPrimitive().isString()) {
				Logger.warn("Must have property 'effect' set to a string");
				return null;
			}

			JsonElement duration = effectJson.get("duration");
			if (duration == null || !duration.isJsonPrimitive() || !duration.getAsJsonPrimitive().isNumber()) {
				Logger.warn("Must have property 'duration' set to a number");
				return null;
			}

			return new ModPotionEffect(effect.getAsString(), duration.getAsInt());
		}
	}

	@Serialized private final String effect;
	@Serialized private final int duration;

	public ModPotionEffect (String which, int duration) {
		super(ForgeRegistries.POTIONS.getValue(new ResourceLocation(which)), duration);
		this.effect = which;
		this.duration = duration;
	}
}
