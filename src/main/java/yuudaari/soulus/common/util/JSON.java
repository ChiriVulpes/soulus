package yuudaari.soulus.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

public class JSON {

	public static String getString (final JsonElement json, final String indent) {
		try {
			final StringWriter stringWriter = new StringWriter();
			final JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			jsonWriter.setIndent(indent == null ? "" : indent);
			Streams.write(json, jsonWriter);
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
}
