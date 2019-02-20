package yuudaari.soulus.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.JsonUtils;

public class JSON {

	public static String getString (final JsonElement json, final String indent) {
		try {
			final StringWriter stringWriter = new StringWriter();
			final JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			jsonWriter.setIndent(indent == null ? "" : indent);
			com.google.gson.internal.Streams.write(json, jsonWriter);
			return stringWriter.toString();

		} catch (final IOException e) {
			return null;
		}
	}

	public static void writeFile (final JsonElement json, final File file) {
		final String itemsJsonString = getString(json, "\t");
		try {
			Files.write(file.toPath(), itemsJsonString.getBytes());
		} catch (IOException e) {
			Logger.error(e);
			Logger.error("Unable to export json file '" + file.toString() + "'");
		}
	}

	public static boolean mergeInto (final JsonElement into, final JsonElement from) {
		if (into.getClass() != from.getClass())
			return false;

		if (into.isJsonObject()) {
			final JsonObject intoObj = into.getAsJsonObject();
			for (final Entry<String, JsonElement> entry : from.getAsJsonObject().entrySet()) {
				final String key = entry.getKey();
				final JsonElement current = intoObj.get(key);
				final JsonElement replacement = entry.getValue();
				if (current == null || (current.getClass() != replacement.getClass() || (!current.isJsonObject() && !current.isJsonArray()))) {
					intoObj.add(key, replacement);

				} else {
					mergeInto(current, replacement);
				}
			}

		} else if (into.isJsonArray()) {
			into.getAsJsonArray().addAll(from.getAsJsonArray());

		} else {
			return false;
		}

		return true;
	}

	public static Set<String> getStringSet (final JsonObject json, final String key) {
		return Streams.stream(JsonUtils.getJsonArray(json, key, new JsonArray()))
			.map(val -> {
				if (val.isJsonPrimitive() && val.getAsJsonPrimitive().isString())
					return val.getAsString();
				throw new JsonParseException("Members of '" + key + "' array must be strings.");
			})
			.collect(Collectors.toSet());
	}
}
