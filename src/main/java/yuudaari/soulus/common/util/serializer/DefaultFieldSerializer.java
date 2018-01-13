package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

public class DefaultFieldSerializer extends FieldSerializer<Object> {

	@Override
	public final JsonElement serialize (final Class<? extends Object> requestedType, final Object object) {
		Logger.scopes.push(requestedType.getSimpleName());

		JsonElement result = null;

		final IClassSerializationHandler<Object> serializer = getClassSerializer(requestedType);
		if (serializer == null) {
			// if there's no serializer for this class, it must be a primitive
			if (requestedType.isPrimitive() || requestedType == String.class || Number.class
				.isAssignableFrom(requestedType)) {
				result = serializePrimitive(requestedType, object);
			} else {
				Logger.warn("Unable to automatically deserialize the type '" + requestedType.getSimpleName() + "'");
			}
		} else {
			// there's a serializer for this class, so try to use it
			try {
				serializeClass(serializer, object, (JsonObject) (result = new JsonObject()));
			} catch (Exception e) {
				Logger.warn("Unable to deserialize class: " + (e.getClass() == Exception.class ? e.getMessage() : e));
			}
		}

		Logger.scopes.pop();

		return result;
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
	 * Serialize a class into a JSON object.
	 */
	@Nullable
	public static final void serializeClass (final IClassSerializationHandler<Object> serializer, final Object instance, final JsonObject jsonObject) {
		serializer.serialize(instance, jsonObject);
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
	private final JsonElement serializePrimitive (final Class<?> requestedType, final Object instance) {
		if (requestedType == boolean.class || requestedType == Boolean.class) {
			return new JsonPrimitive((Boolean) instance);
		} else if (isNumber(requestedType)) {
			if (requestedType == byte.class || requestedType == Byte.class) {
				return new JsonPrimitive((Byte) instance);
			} else if (requestedType == short.class || requestedType == Short.class) {
				return new JsonPrimitive((Short) instance);
			} else if (requestedType == int.class || requestedType == Integer.class) {
				return new JsonPrimitive((Integer) instance);
			} else if (requestedType == long.class || requestedType == Long.class) {
				return new JsonPrimitive((Long) instance);
			} else if (requestedType == float.class || requestedType == Float.class) {
				return new JsonPrimitive((Float) instance);
			} else if (requestedType == double.class || requestedType == Double.class) {
				return new JsonPrimitive((Double) instance);
			}
		} else if (requestedType == String.class) {
			return new JsonPrimitive((String) instance);
		}

		Logger.warn("Can't serialize primitive '" + requestedType.getSimpleName() + "'");
		return null;
	}

	private boolean isNumber (Class<?> cls) {
		return Number.class
			.isAssignableFrom(cls) || cls == byte.class || cls == short.class || cls == int.class || cls == long.class || cls == float.class || cls == double.class;
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

	/**
	* Gets the serialization handler for a field, returns null if the field is not serializable, or the serializer errors
	*/
	@SuppressWarnings("unchecked")
	@Nullable
	public static IClassSerializationHandler<Object> getClassSerializer (final Class<?> classWithSerializableAnnotation) {
		final Serializable serializableClassAnnotation = classWithSerializableAnnotation
			.getAnnotation(Serializable.class);
		if (serializableClassAnnotation == null) return null;

		@SuppressWarnings("rawtypes")
		Class<? extends IClassSerializationHandler> serializerClass = serializableClassAnnotation
			.serializer();
		// use "value" if that's set but the deserializer isn't
		if (serializerClass == DefaultClassSerializer.class && serializableClassAnnotation
			.value() != DefaultClassSerializer.class) {
			serializerClass = (Class<ClassSerializer<?>>) serializableClassAnnotation.value();
		}

		try {
			return serializerClass.newInstance();

		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.warn("Unable to instantiate serializer: " + serializerClass.getSimpleName());
			Logger.error(e);
			return null;
		}
	}
}
