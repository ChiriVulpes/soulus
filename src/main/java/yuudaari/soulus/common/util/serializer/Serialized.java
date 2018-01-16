package yuudaari.soulus.common.util.serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialized {

	@SuppressWarnings("rawtypes")
	Class<? extends FieldSerializer> value () default DefaultFieldSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IFieldSerializationHandler> serializer () default DefaultFieldSerializer.class;

	@SuppressWarnings("rawtypes")
	Class<? extends IFieldDeserializationHandler> deserializer () default DefaultFieldSerializer.class;

	/**
	 * Whether the value should be serialized as "top-level" in the JsonObject.
	 * If false, it will use the name of the field as the Json property to serialize to.
	 * If true, it will serialize into the top level JsonObject.
	 * An error will be thrown if the value returned by the serialization of this field is not a JsonObject.
	 */
	boolean topLevel () default false;

}
