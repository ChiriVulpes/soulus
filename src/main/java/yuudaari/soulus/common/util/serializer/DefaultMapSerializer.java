package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;

public abstract class DefaultMapSerializer<K, T> extends MapSerializer<K, T> {

	public abstract Class<T> getValueClass ();

	@Override
	public JsonElement serializeValue (final T value) throws Exception {
		final IFieldSerializationHandler<Object> serializer = new DefaultFieldSerializer();
		return DefaultClassSerializer.serializeValue(serializer, getValueClass(), false, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserializeValue (final JsonElement value) throws Exception {
		final IFieldDeserializationHandler<Object> deserializer = new DefaultFieldSerializer();
		return (T) DefaultClassSerializer.deserializeValue(deserializer, getValueClass(), false, value);
	}

	public static abstract class OfStringKeys<T> extends MapSerializer.OfStringKeys<T> {

		public abstract Class<T> getValueClass ();

		@Override
		public JsonElement serializeValue (final T value) throws Exception {
			final IFieldSerializationHandler<Object> serializer = new DefaultFieldSerializer();
			return DefaultClassSerializer.serializeValue(serializer, getValueClass(), false, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T deserializeValue (final JsonElement value) throws Exception {
			final IFieldDeserializationHandler<Object> deserializer = new DefaultFieldSerializer();
			return (T) DefaultClassSerializer.deserializeValue(deserializer, getValueClass(), false, value);
		}
	}
}
