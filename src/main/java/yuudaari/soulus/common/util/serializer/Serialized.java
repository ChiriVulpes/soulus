package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.ISerializationHandler;
import yuudaari.soulus.common.util.serializer.Serializer.DefaultSerializer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialized {

	@SuppressWarnings("rawtypes")
	Class<? extends Serializer> value () default DefaultSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends ISerializationHandler> serializer () default DefaultSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IDeserializationHandler> deserializer () default DefaultSerializer.class;
}
