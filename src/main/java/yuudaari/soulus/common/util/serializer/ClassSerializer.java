package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

public abstract class ClassSerializer<T extends Object> implements IClassSerializationHandler<T>, IClassDeserializationHandler<T> {

	@Override
	public abstract void serialize (T instance, JsonObject object);

	@Override
	public abstract T deserialize (T instance, JsonElement element);
}
