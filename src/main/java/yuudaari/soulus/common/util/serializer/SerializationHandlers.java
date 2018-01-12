package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;

public class SerializationHandlers {

	public static interface ISerializationHandler<T> {

		JsonElement serialize (T object);
	}

	public static interface IDeserializationHandler<T extends Object> {

		T deserialize (Class<?> expectedType, JsonElement element);
	}
}
