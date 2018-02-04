package yuudaari.soulus.common.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
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
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import yuudaari.soulus.common.util.CompareJson;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.DefaultFieldSerializer;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;
import yuudaari.soulus.common.config.ConfigFile;
import yuudaari.soulus.common.config.ConfigInjected.Inject;

public class Config {

	public static Map<String, Config> INSTANCES = new HashMap<>();

	private final Map<String, List<Class<?>>> configFileClasses;
	private Map<Field, Class<?>> configInjections = null;
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final String directory;
	private final ASMDataTable asmDataTable;
	private final String id;

	public Config (final ASMDataTable asmDataTable, final String directory, final String id) {
		this.asmDataTable = asmDataTable;
		this.directory = directory;
		this.id = id;
		configFileClasses = getConfigFileClasses(asmDataTable, id);

		INSTANCES.put(id, this);
	}

	/**
	 * Returns the configuration instance of a config class
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T get (final String id, final Class<T> cls) {
		return (T) INSTANCES.get(id).configs.get(cls);
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
	 * Serializes the serializable classes from each config file
	 */
	public void serialize () {
		for (final Map.Entry<String, List<Class<?>>> entry : configFileClasses.entrySet()) {
			Logger.scopes.push("Config Serialization");
			trySerializeConfigFile(entry.getKey(), filterConfigMap(entry.getValue()));
			Logger.scopes.pop();
		}
	}

	/**
	 * Deserializes the serializable classes from each config file
	 */
	public void deserialize () {
		this.configs.clear();

		for (final Map.Entry<String, List<Class<?>>> entry : configFileClasses.entrySet()) {
			final Map<Class<?>, Object> configs = createConfigClassMap(entry.getValue());

			Logger.scopes.push("Config Deserialization");
			tryDeserializeConfigFile(entry.getKey(), configs);
			Logger.scopes.pop();

			this.configs.putAll(configs);
		}

		inject();
	}

	/**
	 * Injects all the configs into fields marked with @ConfigInject.
	 */
	private void inject () {
		if (configInjections == null) {
			configInjections = getConfigInjections(asmDataTable, id);
		}

		for (final Map.Entry<Field, Class<?>> configInjection : configInjections.entrySet()) {
			final Field field = configInjection.getKey();
			try {
				field.set(null, get(configInjection.getValue()));
			} catch (final Exception e) {
				Logger.warn("Unable to inject config '" + configInjection.getValue()
					.getSimpleName() + "' into field: " + field.getName());
			}
		}
	}

	/**
	 * Attempts to deserialize a config file into all of the classes that serialize into it
	 */
	private void trySerializeConfigFile (String filename, final Map<Class<?>, Object> toSerialize) {
		final String profile = getProfile(getConfigFileJson(filename), filename, toSerialize);
		if (profile != null)
			filename = getProfileFilename(filename, profile);

		Logger.scopes.push(filename);

		final JsonObject json = new JsonObject();
		for (final Map.Entry<Class<?>, Object> serializationEntry : toSerialize.entrySet()) {
			trySerializeClass(serializationEntry.getKey(), serializationEntry.getValue(), json);
		}

		final File configFile = new File(directory + filename);
		writeJsonConfigFile(configFile, json, getErrorFilename(configFile.getAbsolutePath()));

		Logger.scopes.pop();
	}

	/**
	 * Gets a default profile of the given config classes.
	 */
	@Nullable
	private String getConfigFileProfile (final Map<Class<?>, Object> toSerialize) {
		final Class<?> cls = toSerialize.keySet().stream().findAny().get();
		final ConfigFile configFileAnnotation = cls.getAnnotation(ConfigFile.class);
		if (configFileAnnotation.profile().equals("")) return null;
		return configFileAnnotation.profile();
	}

	/**
	 * Attempts to deserialize a config file into all of the classes that serialize into it
	 */
	private void tryDeserializeConfigFile (String filename, final Map<Class<?>, Object> toDeserialize) {
		JsonObject json = getConfigFileJson(filename);

		final String profile = getProfile(json, filename, toDeserialize);

		if (profile != null) {
			filename = getProfileFilename(filename, profile);
			json = getConfigFileJson(filename);
		}

		Logger.scopes.push(filename);

		if (json == null)
			Logger.warn("Not a valid Json Object");

		for (final Map.Entry<Class<?>, Object> deserializationEntry : toDeserialize.entrySet()) {
			final Object deserialized = tryDeserializeClass(deserializationEntry.getKey(), json, profile);
			deserializationEntry.setValue(deserialized);
		}

		Logger.scopes.pop();
	}

	/**
	 * Gets the profile for a config file. 
	 * If there is no default config file, sets the default config file's profile to the default config profile.
	 */
	private String getProfile (final JsonObject json, final String filename, final Map<Class<?>, Object> toSerialize) {
		if (json == null) {
			final String profile = getConfigFileProfile(toSerialize);
			if (profile == null) return null;

			final JsonObject jsonProfile = new JsonObject();
			jsonProfile.add("profile", new JsonPrimitive(profile));
			final File file = new File(directory + filename);
			writeJsonConfigFile(file, jsonProfile, getErrorFilename(file.getAbsolutePath()));
			return profile;

		} else {
			final JsonElement jsonProfile = json == null ? null : json.get("profile");
			if (jsonProfile != null && jsonProfile.isJsonPrimitive() && jsonProfile.getAsJsonPrimitive().isString())
				return jsonProfile.getAsString();
		}

		return null;
	}

	/**
	 * Turns the filename of a config file into a profile of the filename
	 */
	private String getProfileFilename (final String filename, final String profile) {
		return new StringBuilder(filename)
			.insert(filename.lastIndexOf('.'), '#' + profile)
			.toString();
	}

	/**
	 * Turns the filename of a config file into a error version
	 */
	private String getErrorFilename (final String filename) {
		return new StringBuilder(filename)
			.insert(filename.lastIndexOf('.'), ".err")
			.toString();
	}

	/**
	 * Gets the JsonObject for a config file
	 */
	private JsonObject getConfigFileJson (final String fileName) {
		final File configFile = new File(directory + fileName);
		if (!configFile.exists())
			createConfigFile(configFile);

		final JsonObject json = parseJsonConfigFile(configFile);

		return json;
	}

	/**
	 * Deserialize all of the @Serialized fields in a class
	 */
	private void trySerializeClass (final Class<?> cls, final Object toSerialize, JsonObject containingObject) {
		Logger.scopes.push(cls.getSimpleName());

		containingObject = getActualContainingObject(containingObject, cls, true);
		if (containingObject != null) {

			final IClassSerializationHandler<Object> deserializer = DefaultFieldSerializer.getClassSerializer(cls);
			if (deserializer != null) {
				try {
					DefaultFieldSerializer.serializeClass(deserializer, toSerialize, containingObject);
				} catch (final Exception e) {
					final boolean isNormalException = e.getClass() == Exception.class;
					Logger.warn("Could not serialize class: " + (isNormalException ? e.getMessage() : ""));
					if (!isNormalException) {
						Logger.error(e);
					}
				}
			} else {
				Logger.warn("Class is not @Serializable");
			}
		}

		Logger.scopes.pop();
	}

	/**
	 * Deserialize all of the @Serialized fields in a class
	 */
	private Object tryDeserializeClass (final Class<?> cls, JsonObject containingObject, final String profile) {
		Logger.scopes.push(cls.getSimpleName());

		Object result = null;

		containingObject = getActualContainingObject(containingObject, cls);

		final IClassDeserializationHandler<Object> deserializer = DefaultFieldSerializer.getClassDeserializer(cls);
		if (deserializer != null) {
			try {
				result = DefaultFieldSerializer.deserializeClass(deserializer, cls, containingObject, profile);
			} catch (final Exception e) {
				Logger
					.warn("Could not deserialize class: " + (e.getClass() == Exception.class ? e.getMessage() : e));
			}
		} else {
			Logger.warn("Class is not @Serializable");
		}

		Logger.scopes.pop();

		return result;
	}

	/**
	 * Gets the containing json object of the serializable class based on the @ConfigFile path. Does not create missing JsonObjects
	 */
	@Nullable
	private JsonObject getActualContainingObject (final JsonObject containingObject, final Class<?> cls) {
		return getActualContainingObject(containingObject, cls, false);
	}

	/**
	 * Gets the containing json object of the serializable class based on the @ConfigFile path
	 */
	@Nullable
	private JsonObject getActualContainingObject (final JsonObject containingObject, final Class<?> cls, final boolean createMissing) {
		JsonObject result = containingObject;

		final String[] propertyPath = ConfigFileUtil.getConfigPropertyPath(cls);

		if (result != null) {
			for (final String property : propertyPath) {
				JsonElement propertyValue = result.get(property);
				if (propertyValue == null || !propertyValue.isJsonObject()) {
					if (createMissing) {
						propertyValue = new JsonObject();
						result.add(property, propertyValue);

					} else {
						result = null;
						break;
					}
				}

				result = propertyValue.getAsJsonObject();
			}
		}

		if (result == null)
			Logger.warn("Config file must include the path: '" + String.join(".", propertyPath) + "'");

		return result;
	}

	/**
	 * Returns the JsonObject of a config file
	 */
	@Nullable
	private static JsonObject parseJsonConfigFile (final File configFile) {
		return parseJsonConfigFile(configFile, true);
	}

	/**
	 * Returns the JsonObject of a config file
	 */
	@Nullable
	private static JsonObject parseJsonConfigFile (final File configFile, boolean warn) {
		try {
			final JsonElement json = new JsonParser().parse(new FileReader(configFile));
			if (json != null && json.isJsonObject())
				return json.getAsJsonObject();

		} catch (final IOException | JsonParseException e) {
			if (warn) Logger.warn("Could not parse the config file: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Replaces the contents of a config file with a string representation of a Json Object.
	 * @param saveOld If not null, and the file contents are changing, the old version is saved to this file.
	 */
	private static void writeJsonConfigFile (final File configFile, final JsonObject json, @Nullable final String saveOld) {
		try {

			final JsonElement oldConfig = parseJsonConfigFile(configFile, false);
			if (CompareJson.equal(json, oldConfig))
				return;

			final StringWriter stringWriter = new StringWriter();
			final JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			jsonWriter.setIndent("\t");
			Streams.write(json, jsonWriter);
			final String newFileText = stringWriter.toString();

			final String oldFileText = new String(Files.readAllBytes(configFile.toPath()));

			if (newFileText.equals(oldFileText))
				return;

			if (saveOld != null && oldFileText.length() > 0)
				Files.write(new File(saveOld).toPath(), oldFileText.getBytes());

			Files.write(configFile.toPath(), newFileText.getBytes());

		} catch (final IOException | JsonParseException e) {
			Logger.warn("Could not write the config file: " + e.getMessage());
		}
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
	private static List<Class<?>> getSerializableClasses (final ASMDataTable asmDataTable) {
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
	 * Returns a list of fields that configs are injected into.
	 */
	private static Map<Field, Class<?>> getConfigInjections (final ASMDataTable asmDataTable, final String id) {
		final Map<Field, Class<?>> result = new HashMap<>();

		final List<Class<?>> classes = getInjectedClasses(asmDataTable, id);
		for (final Class<?> injectionClass : classes) {
			for (final Field field : injectionClass.getDeclaredFields()) {
				final Inject injectAnnotation = field.getAnnotation(Inject.class);
				if (injectAnnotation != null) {
					Class<?> classToInject = injectAnnotation.value() == Inject.class ? field
						.getType() : injectAnnotation.value();
					result.put(field, classToInject);
				}
			}
		}

		return result;
	}

	/**
	* Returns a list of all serializable classes, from the ASM data table
	*/
	private static List<Class<?>> getInjectedClasses (final ASMDataTable asmDataTable, final String id) {
		final List<Class<?>> classes = new ArrayList<>();

		final String annotationClassName = ConfigInjected.class.getCanonicalName();
		final Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);

		for (ASMDataTable.ASMData asmData : asmDatas) {
			try {
				final Class<?> annotatedClass = Class.forName(asmData.getClassName());
				final ConfigInjected injected = annotatedClass.getAnnotation(ConfigInjected.class);
				if (injected.value().equals(id))
					classes.add(annotatedClass);

			} catch (final ClassNotFoundException | LinkageError | NullPointerException e) {
				Logger.warn("Failed to get class from ASM data: " + asmData.getClassName() + e);
			}
		}

		return classes;
	}

	/**
	 * Maps the list of serializable classes to their respective config files
	 */
	private static Map<String, List<Class<?>>> getConfigFileClasses (final ASMDataTable asmDataTable, final String id) {
		Logger.scopes.push("Config File class registration");

		final Map<String, List<Class<?>>> result = new HashMap<>();

		final List<Class<?>> classes = getSerializableClasses(asmDataTable);

		for (final Class<?> cls : classes) {
			final String configFileId = ConfigFileUtil.getConfigId(cls);
			if (!id.equals(configFileId)) {
				continue;
			}

			final String configFile = ConfigFileUtil.getConfigFile(cls);
			if (configFile == null) {
				Logger.warn("Cannot get the config file for '" + cls.getSimpleName() + "'");
				continue;
			}

			List<Class<?>> configFileClasses = result.get(configFile);
			if (configFileClasses == null) {
				result.put(configFile, configFileClasses = new ArrayList<>());
			}

			// Logger.info("Added config class, file: " + configFile + ", class: " + cls.getSimpleName());

			configFileClasses.add(cls);
		}

		Logger.scopes.pop();

		return result;
	}

	/**
	 * Filters the config map to only include classes specified in the list
	 */
	private Map<Class<?>, Object> filterConfigMap (List<Class<?>> validKeys) {
		return configs.entrySet()
			.stream()
			.filter(e -> validKeys.contains(e.getKey()))
			.collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
	}
}
