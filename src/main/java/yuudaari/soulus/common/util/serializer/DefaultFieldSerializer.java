package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;

public class DefaultFieldSerializer extends FieldSerializer<Object> {

	@Override
	public final JsonElement serialize (final Object object) {
		return null;
	}

	/**
	 * Deserialize a field of the requested type from a JsonElement value
	 */
	@Override
	public final Object deserialize (final Class<?> requestedType, final JsonElement element) {
		Logger.scopes.push(requestedType.getSimpleName());

		Object result = null;

		final IClassDeserializationHandler<Object> deserializer = getClassDeserializer(requestedType);
		if (deserializer == null) {
			// if there's no deserializer for this class, it must be a primitive
			if (requestedType.isPrimitive() || requestedType == String.class || Number.class
				.isAssignableFrom(requestedType)) {
				if (element.isJsonPrimitive()) {
					result = deserializePrimitive(requestedType, element);
				} else {
					Logger.warn("Json value is the wrong type");
				}
			} else {
				Logger.warn("Unable to automatically deserialize the type '" + requestedType.getSimpleName() + "'");
			}
		} else {
			// there's a deserializer for this class, so try to use it
			try {
				result = deserializeClass(deserializer, requestedType, element);
			} catch (Exception e) {
				Logger.warn("Unable to deserialize class: " + (e.getClass() == Exception.class ? e.getMessage() : e));
			}
		}

		Logger.scopes.pop();

		return result;
	}

	/**
	 * Instantiate a class, and then deserialize a JSON value into it.
	 */
	@Nullable
	public static final Object deserializeClass (final IClassDeserializationHandler<Object> deserializer, final Class<?> requestedType, final JsonElement element) {
		final Object instance = deserializer.instantiate(requestedType);
		return deserializer.deserialize(instance, element);
	}

	/**
	 * Deserializes a primitive JSON value. Returns null if the requested type and given JSON value do not match.
	 */
	@Nullable
	private final Object deserializePrimitive (final Class<?> requestedType, final JsonElement element) {
		final JsonPrimitive primitive = element.getAsJsonPrimitive();

		if (primitive.isBoolean() && requestedType == boolean.class || requestedType == Boolean.class) {
			return (Boolean) primitive.getAsBoolean();
		} else if (primitive.isNumber()) {
			if (requestedType == byte.class || requestedType == Byte.class) {
				return (Byte) primitive.getAsByte();
			} else if (requestedType == short.class || requestedType == Short.class) {
				return (Short) primitive.getAsShort();
			} else if (requestedType == int.class || requestedType == Integer.class) {
				return (Integer) primitive.getAsInt();
			} else if (requestedType == long.class || requestedType == Long.class) {
				return (Long) primitive.getAsLong();
			} else if (requestedType == float.class || requestedType == Float.class) {
				return (Float) primitive.getAsFloat();
			} else if (requestedType == double.class || requestedType == Double.class) {
				return (Double) primitive.getAsDouble();
			}
		} else if (primitive.isString()) {
			return primitive.getAsString();
		}

		Logger.warn("Can't deserialize primitive '" + primitive.getClass().getSimpleName() + "'");
		return null;
	}

	/**
	* Gets the deserialization handler for a field, returns null if the field is not serializable, or the deserializer errors
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public static IClassDeserializationHandler<Object> getClassDeserializer (final Class<?> classWithSerializableAnnotation) {
		final Serializable serializableClassAnnotation = classWithSerializableAnnotation
			.getAnnotation(Serializable.class);
		if (serializableClassAnnotation == null) return null;

		@SuppressWarnings("rawtypes")
		Class<? extends IClassDeserializationHandler> deserializerClass = serializableClassAnnotation
			.deserializer();
		// use "value" if that's set but the deserializer isn't
		if (deserializerClass == DefaultClassSerializer.class && serializableClassAnnotation
			.value() != DefaultClassSerializer.class) {
			deserializerClass = (Class<ClassSerializer<?>>) serializableClassAnnotation.value();
		}

		try {
			return deserializerClass.newInstance();

		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.warn("Unable to instantiate deserializer: " + deserializerClass.getSimpleName());
			Logger.error(e);
			return null;
		}
	}
}
