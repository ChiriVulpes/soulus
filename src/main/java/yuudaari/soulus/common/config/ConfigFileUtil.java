package yuudaari.soulus.common.config;

import javax.annotation.Nullable;

public class ConfigFileUtil {

	/**
	 * Gets the name of the config file that this class should be serialized into
	 */
	@Nullable
	public static String getConfigFile (final Class<?> classWithConfigFileAnnotation) {
		try {
			final String file = classWithConfigFileAnnotation.getAnnotation(ConfigFile.class).file();
			return file.endsWith(".json") ? file : file + ".json";
		} catch (final NullPointerException e) {
			return null;
		}
	}

	/**
	 * Gets the name of the config file that this class should be serialized into
	 */
	@Nullable
	public static String getConfigId (final Class<?> classWithConfigFileAnnotation) {
		try {
			final String id = classWithConfigFileAnnotation.getAnnotation(ConfigFile.class).id();
			return id;
		} catch (final NullPointerException e) {
			return null;
		}
	}

	/**
	 * Gets the property path of the json that this class should be serialized into
	 */
	public static String[] getConfigPropertyPath (final Class<?> classWithConfigFileAnnotation) {
		try {
			final String property = classWithConfigFileAnnotation.getAnnotation(ConfigFile.class).path();
			if (!property.equals(""))
				return property.split("\\.");
		} catch (final NullPointerException e) {}

		return new String[0];
	}
}
