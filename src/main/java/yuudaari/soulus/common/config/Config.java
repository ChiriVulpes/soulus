package yuudaari.soulus.common.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.config.ConfigFile;

public class Config {

	private final Map<String, List<Class<?>>> configFileClasses;
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final String directory;

	public Config (final ASMDataTable asmDataTable, final String directory) {
		this.directory = directory;
		configFileClasses = getConfigFileClasses(asmDataTable);
	}

	/**
	 * Returns the configuration instance of a config class
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T get (final Class<T> cls) {
		final Object result = configs.get(cls);
		if (result == null || !cls.isInstance(result))
			return null;

		return (T) result;
	}

	/**
	 * Deserializes the serializable classes from each config file
	 */
	public void deserialize () {
		for (final Map.Entry<String, List<Class<?>>> entry : configFileClasses.entrySet()) {
			final Map<Class<?>, Object> configs = createConfigClassMap(entry.getValue());

			Logger.scopes.push("Config Deserialization");
			tryDeserializeConfigFile(entry.getKey(), configs);
			Logger.scopes.pop();

			this.configs.putAll(configs);
		}
	}


	/**
	 * Attempts to deserialize a config file into all of the classes that serialize into it
	 */
	private void tryDeserializeConfigFile (final String fileName, final Map<Class<?>, Object> toDeserialize) {
		Logger.scopes.push(fileName);

		final File configFile = new File(directory + fileName);
		if (configFile.exists()) {
			final JsonObject json = parseJsonConfigFile(configFile);
			if (json == null) {
				Logger.warn("Not a valid Json Object");
			} else {
				for (final Map.Entry<Class<?>, Object> deserializationEntry : toDeserialize.entrySet()) {
					deserializationEntry
						.setValue(ConfigDeserialization.tryDeserializeClass(deserializationEntry.getKey(), json));
				}
			}
		} else {
			createConfigFile(configFile);
		}

		Logger.scopes.pop();
	}

	/**
	 * Returns the JsonObject of a config file
	 */
	@Nullable
	private static JsonObject parseJsonConfigFile (final File configFile) {
		try {
			final JsonElement json = new JsonParser().parse(new FileReader(configFile));
			if (json != null && json.isJsonObject())
				return json.getAsJsonObject();

		} catch (final IOException | JsonParseException e) {
			Logger.warn("Could not parse the config file: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Creates a config file
	 */
	private boolean createConfigFile (final File configFile) {
		try {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			return true;

		} catch (IOException e) {
			// if we error, we don't worry about the file and instead just use the base configs
			Logger.warn("Could not create the config file: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Instantiates instances of the serializable classes
	 */
	private Map<Class<?>, Object> createConfigClassMap (final List<Class<?>> classes) {
		final Map<Class<?>, Object> result = new HashMap<>();

		for (final Class<?> cls : classes) {
			result.put(cls, null);
		}

		return result;
	}

	/**
	 * Returns a list of all serializable classes, from the ASM data table
	 */
	private List<Class<?>> getSerializableClasses (final ASMDataTable asmDataTable) {
		final List<Class<?>> classes = new ArrayList<>();

		final String annotationClassName = ConfigFile.class.getCanonicalName();
		final Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);

		for (ASMDataTable.ASMData asmData : asmDatas) {
			try {
				final Class<?> asmClass = Class.forName(asmData.getClassName());
				classes.add(asmClass);

			} catch (final ClassNotFoundException | LinkageError e) {
				Logger.warn("Failed to get class from ASM data: " + asmData.getClassName() + e);
			}
		}

		return classes;
	}

	/**
	 * Maps the list of serializable classes to their respective config files
	 */
	private Map<String, List<Class<?>>> getConfigFileClasses (final ASMDataTable asmDataTable) {
		Logger.scopes.push("Config File class registration");

		final Map<String, List<Class<?>>> result = new HashMap<>();

		final List<Class<?>> classes = getSerializableClasses(asmDataTable);

		for (final Class<?> cls : classes) {
			final String configFile = ConfigFileUtil.getConfigFile(cls);
			if (configFile == null) {
				Logger.warn("Cannot get the config file for '" + cls.getSimpleName() + "'");
				continue;
			}

			List<Class<?>> configFileClasses = result.get(configFile);
			if (configFileClasses == null) {
				result.put(configFile, configFileClasses = new ArrayList<>());
			}

			Logger.info("Added config class, file: " + configFile + ", class: " + cls.getSimpleName());

			configFileClasses.add(cls);
		}

		Logger.scopes.pop();

		return result;
	}
}
