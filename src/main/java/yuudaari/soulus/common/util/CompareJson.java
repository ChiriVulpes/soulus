package yuudaari.soulus.common.util;

import java.util.Map.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CompareJson {

	public static boolean equal (JsonElement element1, JsonElement element2) {
		boolean result = true;

		if (element1 == null || element2 == null) return (element1 == null) == (element2 == null);

		if (element1.getClass() != element2.getClass())
			result = false;

		else if (element1.isJsonNull())
			return true;

		else if (element1.isJsonPrimitive())
			result = equal(element1.getAsJsonPrimitive(), element2.getAsJsonPrimitive());

		else if (element1.isJsonObject())
			result = equal(element1.getAsJsonObject(), element2.getAsJsonObject());

		else if (element1.isJsonArray())
			result = equal(element1.getAsJsonArray(), element2.getAsJsonArray());

		return result;
	}

	private static boolean equal (JsonObject obj1, JsonObject obj2) {
		for (Entry<String, JsonElement> entry : obj1.entrySet()) {
			if (!obj2.has(entry.getKey())) return false;

			if (!equal(entry.getValue(), obj2.get(entry.getKey()))) return false;
		}

		return true;
	}

	private static boolean equal (JsonArray arr1, JsonArray arr2) {
		if (arr1.size() != arr2.size()) return false;

		for (int i = 0; i < arr1.size(); i++) {
			if (!equal(arr1.get(i), arr2.get(i))) return false;
		}

		return true;
	}

	private static boolean equal (JsonPrimitive prim1, JsonPrimitive prim2) {

		if (prim1.isNumber() && prim2.isNumber())
			return prim1.getAsNumber().equals(prim2.getAsNumber()) || //
				prim1.getAsNumber().toString().equals(prim2.getAsNumber().toString());

		if (prim1.isString() && prim2.isString())
			return prim1.getAsString().equals(prim2.getAsString());

		if (prim1.isBoolean() && prim2.isBoolean())
			return prim1.getAsBoolean() == prim2.getAsBoolean();

		return false;
	}
}
