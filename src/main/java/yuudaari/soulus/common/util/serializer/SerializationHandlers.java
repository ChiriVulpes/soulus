package yuudaari.soulus.common.util.serializer;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.config.CaseConversion;
import yuudaari.soulus.common.config.ConfigProfile;
import yuudaari.soulus.common.util.Logger;

public class SerializationHandlers {

	public static interface IFieldSerializationHandler<T> {

		JsonElement serialize (Class<?> objectType, T object);
	}

	public static interface IFieldDeserializationHandler<T extends Object> {

		T deserialize (Class<?> expectedType, JsonElement element);
	}

	public static interface IClassSerializationHandler<T> {

		void serialize (T object, JsonObject into);
	}

	public static interface IClassDeserializationHandler<T extends Object> {

		/**
		 * Instantiate a class. Returns null if it's unable to be instantiated.
		 */
		@SuppressWarnings("unchecked")
		@Nullable
		default T instantiate (final Class<?> cls) {
			Object instance = null;
			try {
				instance = cls.newInstance();
			} catch (final InstantiationException | IllegalAccessException e) {
				Logger.warn("Could not instantiate: ");
				Logger.error(e);
			}

			return (T) instance;
		}

		@SuppressWarnings("unchecked")
		@Nullable
		default T getProfile (final Class<?> cls, final String profileName) {
			for (final Field field : cls.getFields()) {
				// the field must be of the requested type
				if (field.getType() != cls) continue;

				// the field must be annotated with @ConfigProfile
				final ConfigProfile profile = field.getAnnotation(ConfigProfile.class);
				if (profile == null) continue;

				// get the profile name
				String name = profile.value();
				if (name.equals("")) name = CaseConversion.toSnakeCase(field.getName());

				// check if this is the profile we're looking for
				if (name.equals(profileName)) {
					try {
						return (T) field.get(null);
					} catch (final IllegalAccessException e) {}
				}
			}

			Logger.warn("Unable to find a default profile object for the profile '" + profileName + "'");
			return null;
		}

		T deserialize (@Nullable T instance, JsonElement element);
	}
}
