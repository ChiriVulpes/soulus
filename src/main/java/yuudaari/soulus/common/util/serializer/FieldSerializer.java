package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IFieldSerializationHandler;

public abstract class FieldSerializer<T> implements IFieldSerializationHandler<T>, IFieldDeserializationHandler<T> {

	@Override
	public abstract JsonElement serialize (final T object);

	@Override
	public abstract T deserialize (Class<?> requestedType, JsonElement element);
}
