package yuudaari.soulus.common.util.serializer;

import java.lang.reflect.Field;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.config.CaseConversion;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;

public class DefaultClassSerializer extends ClassSerializer<Object> {

	@Override
	public JsonElement serialize (Object object) {
		return null;
	}

	/**
	 * Deserialize a JSON element into a given object instance.
	 */
	@Override
	public Object deserialize (final Object instance, final JsonElement element) {
		if (element == null || !element.isJsonObject()) {
			Logger.warn("Json value must be an object. Using base instance.");
			return instance;
		}

		for (final Field field : instance.getClass().getFields()) {
			tryDeserializeField(element.getAsJsonObject(), field, instance);
		}

		return instance;
	}

	/**
	 * Deserialize a field into the instance object
	 */
	private static void tryDeserializeField (JsonObject containingObject, final Field field, final Object instance) {
		Logger.scopes.push(field.getName());

		final IFieldDeserializationHandler<Object> deserializer = getFieldDeserializer(field);
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
	 * Instantiate a class. Returns null if it's unable to be instantiated.
	 */
	@Override
	@Nullable
	public Object instantiate (final Class<?> cls) {
		Object instance = null;
		try {
			instance = cls.newInstance();
		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.warn("Could not instantiate: " + e);
		}

		return instance;
	}

	/**
	 * Gets the deserialization handler for a field, returns null if the field is not serializable, or the deserializer errors
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	private static IFieldDeserializationHandler<Object> getFieldDeserializer (final Field field) {
		final Serialized serializableClassAnnotation = field.getAnnotation(Serialized.class);
		if (serializableClassAnnotation == null) return null;

		@SuppressWarnings("rawtypes")
		Class<? extends IFieldDeserializationHandler> deserializerClass = serializableClassAnnotation.deserializer();
		// use "value" if that's set but the deserializer isn't
		if (deserializerClass == DefaultFieldSerializer.class && serializableClassAnnotation
			.value() != DefaultFieldSerializer.class) {
			deserializerClass = (Class<DefaultFieldSerializer>) serializableClassAnnotation.value();
		}

		try {
			return deserializerClass.newInstance();

		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.warn("Unable to instantiate deserializer");
			Logger.error(e);
			return null;
		}
	}
}
