package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

@Retention(RetentionPolicy.RUNTIME)
public @interface Serializable {

	@SuppressWarnings("rawtypes")
	Class<? extends ClassSerializer> value () default DefaultClassSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IClassSerializationHandler> serializer () default DefaultClassSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IClassDeserializationHandler> deserializer () default DefaultClassSerializer.class;

	/**
	 * Note: Untested
	 * @param instance (Static-only) The instance of this class being initialized.
	 * @param element The JSON element being deserialized.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface OnDeserialize {
	}

	/**
	 * Note: Untested
	 * @param instance (Static-only) The instance of this class.
	 * @param element The JSON element being serialized to.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface OnSerialize {
	}
}
