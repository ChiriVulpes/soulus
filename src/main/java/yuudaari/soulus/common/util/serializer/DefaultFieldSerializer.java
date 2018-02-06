package yuudaari.soulus.common.util.serializer;

import java.lang.reflect.Array;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
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
			result = trySerializePrimitive(requestedType, object);
			if (result == null) result = trySerializeArray(requestedType, object);
			if (result == null) {
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
			// there's no deserializer registered for this class
			result = tryDeserializePrimitive(requestedType, element);
			if (result == null) result = tryDeserializeArray(requestedType, element);
			if (result == null) {
				Logger.warn("Unable to automatically deserialize the type '" + requestedType.getSimpleName() + "'");
			}
		} else {
			// there's a deserializer registered for this class, so try to use it
			try {
				result = deserializeClass(deserializer, requestedType, element, null);
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
	public static final Object deserializeClass (final IClassDeserializationHandler<Object> deserializer, final Class<?> requestedType, final JsonElement element, @Nullable final String profile) {
		Object instance = null;

		if (profile != null)
			instance = deserializer.getProfile(requestedType, profile);

		if (instance == null)
			instance = deserializer.instantiate(requestedType);

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
	 * Attempts to deserialize a primitive. 
	 * If the requested type is not a primitive, or the types don't line up, returns null.
	 */
	@Nullable
	private final Object tryDeserializePrimitive (final Class<?> requestedType, final JsonElement element) {
		if (requestedType.isPrimitive() || requestedType == String.class || Number.class
			.isAssignableFrom(requestedType)) {
			if (element.isJsonPrimitive()) {
				return deserializePrimitive(requestedType, element);
			} else {
				Logger.warn("Json value is the wrong type");
			}
		}

		return null;
	}

	/**
	 * Attempts to serialize a primitive. 
	 * If the requested type is not a primitive, returns null.
	 */
	@Nullable
	private final JsonElement trySerializePrimitive (final Class<?> requestedType, final Object object) {
		if (requestedType.isPrimitive() || requestedType == String.class || Number.class
			.isAssignableFrom(requestedType)) {
			return serializePrimitive(requestedType, object);
		}

		return null;
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
	 * Attempts to serialize an array. 
	 * Returns null if the requested type is not an array.
	 */
	@Nullable
	private final JsonElement trySerializeArray (final Class<?> requestedType, final Object object) {
		if (requestedType.isArray()) {
			return serializeArray(requestedType, object);
		}

		return null;
	}

	/**
	 * Attempts to deserialize an array. 
	 * Returns null if the requested type is not an array, or the types don't match.
	 */
	@Nullable
	private final Object tryDeserializeArray (final Class<?> requestedType, final JsonElement element) {
		if (requestedType.isArray()) {
			if (element.isJsonArray()) {
				return deserializeArray(requestedType, element.getAsJsonArray());
			} else {
				Logger.warn("Json value is not an array");
			}
		}

		return null;
	}

	/**
	 * Serializes an array. Skips any entries which are "null".
	 */
	@Nullable
	private final JsonArray serializeArray (final Class<?> requestedType, final Object object) {
		final Class<?> containedType = requestedType.getComponentType();

		final Object[] arr = (Object[]) object;

		final JsonArray result = new JsonArray();

		for (int i = 0; i < arr.length; i++) {
			JsonElement value = serialize(containedType, arr[i]);
			if (value == null) continue;
			result.add(value);
		}

		return result;
	}

	/**
	 * Deserializes an array. If any of the values are null, returns null for the entire array.
	 */
	@Nullable
	private final Object deserializeArray (final Class<?> requestedType, final JsonArray element) {
		final Class<?> containedType = requestedType.getComponentType();

		final Object[] result = (Object[]) Array.newInstance(containedType, element.size());

		for (int i = 0; i < element.size(); i++) {
			Object value = deserialize(containedType, element.get(i));
			if (value == null) return null;
			result[i] = value;
		}

		return result;
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
