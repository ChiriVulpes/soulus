package yuudaari.soulus.common.util.serializer;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.util.Logger;

public abstract class MapSerializer<K, V> extends FieldSerializer<Map<K, V>> {

	/**
	 * Serialize a map instance into a Json object.
	 */
	@Override
	public JsonElement serialize (final Class<?> objectType, final Map<K, V> object) {
		JsonObject result = new JsonObject();

		try {
			for (final Map.Entry<K, V> entry : object.entrySet()) {
				final String key = serializeKey(entry.getKey());
				final JsonElement value = serializeValue(entry.getValue());
				result.add(key, value);
			}
		} catch (final Exception e) {
			Logger.warn("Couldn't serialize map:");
			Logger.error(e);

			result = new JsonObject();
		}

		return result;
	}


	/**
	 * Deserialize a Json element.
	 */
	@Override
	public Map<K, V> deserialize (final Class<?> requestedType, final JsonElement json) {
		if (json == null || !(json instanceof JsonObject)) {
			Logger.warn("Not a Json Object");
			return null;
		}

		Map<K, V> map = new HashMap<>();

		try {
			for (final Map.Entry<String, JsonElement> jsonConfig : json.getAsJsonObject().entrySet()) {
				final K key = deserializeKey(jsonConfig.getKey());
				final V value = deserializeValue(jsonConfig.getValue());
				map.put(key, value);
			}

		} catch (final Exception e) {
			Logger.warn("Unable to deserialize map:");
			Logger.error(e);

			map = new HashMap<>();
		}

		return map;
	}

	public abstract String serializeKey (K key);

	public abstract K deserializeKey (String key);

	public abstract JsonElement serializeValue (V value) throws Exception;

	public abstract V deserializeValue (JsonElement value) throws Exception;

	public static abstract class OfStringKeys<V> extends MapSerializer<String, V> {

		@Override
		public String serializeKey (String key) {
			return key;
		}

		@Override
		public String deserializeKey (String key) {
			return key;
		}
	}

}
