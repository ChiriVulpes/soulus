package yuudaari.soulus.common.util.serializer;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;

public abstract class ListSerializer<T> extends FieldSerializer<List<T>> {

	/**
	 * Override
	 */
	public Class<T> getValueClass () {
		return null;
	}

	/**
	 * Serializes a list into a Json Array.
	 */
	@Override
	public final JsonElement serialize (final Class<?> objectType, final List<T> object) {
		JsonArray result = new JsonArray();

		try {
			for (final T item : object) {
				result.add(serializeValue(item));
			}

		} catch (final Exception e) {
			Logger.warn("Couldn't serialize list:");
			Logger.error(e);

			result = new JsonArray();
		}

		return result;
	}

	/**
	 * Deserializes a Json Array into a list.
	 */
	@Override
	public final List<T> deserialize (Class<?> requestedType, JsonElement json) {
		if (json == null || !json.isJsonArray()) {
			Logger.warn("Not a Json Array");
			return null;
		}


		List<T> result = new ArrayList<>();

		try {
			for (final JsonElement item : json.getAsJsonArray()) {
				result.add(deserializeValue(item));
			}

		} catch (final Exception e) {
			Logger.warn("Couldn't serialize list:");
			Logger.error(e);

			result = new ArrayList<>();
		}

		return result;
	}

	public JsonElement serializeValue (final T value) throws Exception {
		final Class<?> valueClass = getValueClass();
		if (valueClass == null)
			throw new Exception("List serializers that use default value serialization must implement getValueClass()");

		final IFieldSerializationHandler<Object> serializer = new DefaultFieldSerializer();
		return DefaultClassSerializer.serializeValue(serializer, valueClass, false, value);
	}

	@SuppressWarnings("unchecked")
	public T deserializeValue (final JsonElement value) throws Exception {
		final Class<?> valueClass = getValueClass();
		if (valueClass == null)
			throw new Exception("List serializers that use default value serialization must implement getValueClass()");

		final IFieldDeserializationHandler<Object> deserializer = new DefaultFieldSerializer();
		return (T) DefaultClassSerializer.deserializeValue(deserializer, valueClass, false, value);
	}

	public static class OfStrings extends ListSerializer<String> {

		@Override
		public Class<String> getValueClass () {
			return String.class;
		}
	}
}
