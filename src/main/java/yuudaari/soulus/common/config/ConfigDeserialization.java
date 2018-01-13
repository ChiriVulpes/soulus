package yuudaari.soulus.common.config;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.util.serializer.DefaultFieldSerializer;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.Logger;

public class ConfigDeserialization {

	/**
	 * Deserialize all of the @Serialized fields in a class
	 */
	public static Object tryDeserializeClass (final Class<?> cls, JsonObject containingObject) {
		Logger.scopes.push(cls.getSimpleName());

		Object result = null;

		containingObject = getActualContainingObject(containingObject, cls);
		if (containingObject != null) {

			final IClassDeserializationHandler<Object> deserializer = DefaultFieldSerializer.getClassDeserializer(cls);
			if (deserializer != null) {
				try {
					result = DefaultFieldSerializer.deserializeClass(deserializer, cls, containingObject);
				} catch (Exception e) {
					Logger
						.warn("Could not deserialize class: " + (e.getClass() == Exception.class ? e.getMessage() : e));
				}
			} else {
				Logger.warn("Class is not @Serializable");
			}
		}

		Logger.scopes.pop();

		return result;
	}

	/**
	 * Gets the containing json object of the serializable class based on the @ConfigFile path
	 */
	@Nullable
	private static JsonObject getActualContainingObject (final JsonObject containingObject, final Class<?> cls) {
		JsonObject result = containingObject;

		final String[] propertyPath = ConfigFileUtil.getConfigProperty(cls);

		if (result != null) {
			for (final String property : propertyPath) {
				final JsonElement propertyValue = result.get(property);
				if (propertyValue == null || !propertyValue.isJsonObject()) {
					result = null;
					break;
				}

				result = propertyValue.getAsJsonObject();
			}
		}

		if (result == null)
			Logger.warn("Config file must include the path: '" + String.join(".", propertyPath) + "'");

		return result;
	}
}
