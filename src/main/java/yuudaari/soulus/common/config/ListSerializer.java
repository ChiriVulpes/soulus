package yuudaari.soulus.common.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.List;
import yuudaari.soulus.common.util.Logger;

public class ListSerializer extends Serializer<List<String>> {

	@Override
	public JsonElement serialize(Object obj) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) obj;

		JsonArray result = new JsonArray();
		for (String item : list)
			result.add(item);

		return result;
	}

	@Override
	public List<String> deserialize(JsonElement listElement, Object currentObject) {
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) currentObject;

		if (listElement == null || !listElement.isJsonArray()) {
			Logger.warn("Must be a list of strings");
			return list;
		}
		JsonArray jsonList = listElement.getAsJsonArray();
		list.clear();

		for (JsonElement item : jsonList) {
			if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
				Logger.warn("Must contain only strings");
				continue;
			}
			list.add(item.getAsString());
		}

		return list;

	}
}
