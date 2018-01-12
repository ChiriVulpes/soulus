package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.ISerializationHandler;
import yuudaari.soulus.common.util.serializer.Serializer.DefaultSerializer;

@Retention(RetentionPolicy.RUNTIME)
public @interface Serializable {

	@SuppressWarnings("rawtypes")
	Class<? extends Serializer> value () default DefaultSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends ISerializationHandler> serializer () default DefaultSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IDeserializationHandler> deserializer () default DefaultSerializer.class;
}
