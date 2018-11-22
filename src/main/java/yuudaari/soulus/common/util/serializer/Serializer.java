package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

public class Serializer {

	public static JsonElement serialize (final Object serializableObject) {
		try {
			final Class<?> cls = serializableObject.getClass();

			final IClassSerializationHandler<Object> deserializer = DefaultFieldSerializer.getClassSerializer(cls);

			final JsonObject json = new JsonObject();
			DefaultFieldSerializer.serializeClass(deserializer, serializableObject, json);

			return json;

		} catch (Exception e) {
			Logger.error("Unable to serialize generic object");
			Logger.error(e);

			return JsonNull.INSTANCE;
		}
	}
}
