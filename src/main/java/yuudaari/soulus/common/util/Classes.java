package yuudaari.soulus.common.util;

public class Classes {

	public static <T> T instantiate (final Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (final InstantiationException | IllegalAccessException e) {
			Logger.error(e);
			Logger.warn("Cannot instantiate class " + cls.getName());
			return null;
		}
	}
}
