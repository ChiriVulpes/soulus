package yuudaari.soulus.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInjected {

	/**
	 * The ID of the Config instance the injection classes were serialized with.
	 */
	String value ();


	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Inject {

		/**
		 * The class that this injector will inject an instance of.
		 */
		Class<?> value ();
	}
}
