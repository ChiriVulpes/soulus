package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ClassSerializationEventHandlers {

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface SerializationEventHandler {}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DeserializationEventHandler {}
}

