package yuudaari.soulus.common.config;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.util.serializer.Serialized;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IDeserializationHandler;
import yuudaari.soulus.common.util.serializer.Serializer.DefaultSerializer;
import yuudaari.soulus.common.util.Logger;

public class ConfigDeserialization {

	/**
	 * Deserialize all of the @Serialized fields in a class
	 */
	public static void tryDeserializeClass (JsonObject containingObject, final Class<?> cls, final Object instance) {
		Logger.scopes.push(cls.getSimpleName());

		containingObject = getActualContainingObject(containingObject, cls);
		if (containingObject != null) {
			for (final Field field : cls.getFields()) {
				tryDeserializeField(containingObject, field, instance);
			}
		}

		Logger.scopes.pop();
	}

	/**
	 * Deserialize a field into the instance object
	 */
	private static void tryDeserializeField (JsonObject containingObject, final Field field, final Object instance) {
		Logger.scopes.push(field.getName());

		final IDeserializationHandler<?> deserializer = getDeserializer(field);
		if (deserializer != null) {
			final String jsonFieldName = CaseConversion.toSnakeCase(field.getName());
			try {
				final JsonElement jsonValue = containingObject.get(jsonFieldName);
				Object deserializedValue = null;

				if (jsonValue == null || jsonValue.isJsonNull()) {
					final boolean isPrimitive = field.getType().isPrimitive();
					if (!field.isAnnotationPresent(Nullable.class)) {
						throw new Exception("Recieved null, field cannot be null.");

					} else if (isPrimitive) {
						throw new Exception("Recieved null, primitive fields cannot be null.");
					}
				} else {
					deserializedValue = deserializer.deserialize(field.getType(), jsonValue);
				}

				field.set(instance, deserializedValue);

			} catch (final Exception e) {
				Logger.warn("Could not deserialize field: " + (e.getClass() == Exception.class ? e.getMessage() : e));
			}
		}

		Logger.scopes.pop();
	}

	/**
	 * Gets the deserialization handler for a field, returns null if the field is not serializable, or the deserializer errors
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	private static IDeserializationHandler<?> getDeserializer (final Field field) {
		final Serialized serializableClassAnnotation = field.getAnnotation(Serialized.class);
		if (serializableClassAnnotation == null) return null;

		@SuppressWarnings("rawtypes")
		Class<? extends IDeserializationHandler> deserializerClass = serializableClassAnnotation.deserializer();
		// use "value" if that's set but the deserializer isn't
		if (deserializerClass == DefaultSerializer.class && serializableClassAnnotation
			.value() != DefaultSerializer.class) {
			deserializerClass = (Class<DefaultSerializer>) serializableClassAnnotation.value();
		}

		try {
			return deserializerClass.newInstance();

		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.warn("Unable to instantiate deserializer: " + e);
			return null;
		}
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
