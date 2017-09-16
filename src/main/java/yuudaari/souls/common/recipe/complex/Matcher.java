package yuudaari.souls.common.recipe.complex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.nbt.NBTTagCompound;
import yuudaari.souls.Souls;
import java.util.HashMap;
import java.util.Map;

public class Matcher {
	public String type;
	public Variable save;
	public Matcher[] orMatchers;
	public Matcher[] andMatchers;
	public Object matcher;

	public boolean matches(Object thing) {
		if (matcher != null) {
			return matcher.equals(thing);
		}
		return thing.getClass().getName() == type;
	}

	public static Matcher deserialize(JsonObject json) {
		Matcher result = new Matcher();
		if (json.has("@type")) {
			result.type = json.get("@type").getAsString();
		} else {
			if (json.has("@or")) {
				JsonArray arr = json.get("@or").getAsJsonArray();
				for (JsonElement obj : arr) {
					Souls.LOGGER.info(obj);
				}
			}
		}
		return result;
	}

	private static Map<String, String> typeMap = new HashMap<>();
	static {
		typeMap.put("nbt", NBTTagCompound.class.getName());
		typeMap.put("number", Number.class.getName());
		typeMap.put("string", String.class.getName());
		typeMap.put("boolean", Boolean.class.getName());
	}

}