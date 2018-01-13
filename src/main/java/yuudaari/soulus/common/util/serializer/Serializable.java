package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
}
