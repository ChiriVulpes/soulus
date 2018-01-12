package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.common.config.ConfigDeserialization;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.ISerializationHandler;

public abstract class Serializer<T> implements ISerializationHandler<T>, IDeserializationHandler<T> {

	public static final class DefaultSerializer extends Serializer<Object> {

		@Override
		public final JsonElement serialize (final Object object) {
			return null;
		}

		@Override
		public final Object deserialize (final Class<?> requestedType, final JsonElement element) {
			final IDeserializationHandler<?> deserializer = getDeserializer(requestedType);
			if (deserializer != null) {
				if (requestedType.isPrimitive() || requestedType == String.class) {
					if (element.isJsonPrimitive()) {
						return deserializePrimitive(requestedType, element);
					} else {
						Logger.warn("Json value is the wrong type");
					}
				} else {
					Logger.warn("Unable to automatically deserialize the type '" + requestedType.getSimpleName() + "'");
				}
			} else {
				deserializer.deserialize(requestedType, element)
			}

			return null;
		}

		/**
		* Gets the deserialization handler for a field, returns null if the field is not serializable, or the deserializer errors
		*/
		@SuppressWarnings("unchecked")
		@Nullable
		public static IDeserializationHandler<?> getDeserializer (final Class<?> classWithSerializableAnnotation) {
			final Serializable serializableClassAnnotation = classWithSerializableAnnotation
				.getAnnotation(Serializable.class);
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
	};

	@Override
	public abstract JsonElement serialize (final T object);

	@Override
	public abstract T deserialize (final Class<?> requestedType, final JsonElement element);
}
