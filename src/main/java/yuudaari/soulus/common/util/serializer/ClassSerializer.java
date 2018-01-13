package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassDeserializationHandler;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

public abstract class ClassSerializer<T extends Object> implements IClassSerializationHandler<T>, IClassDeserializationHandler<T> {

	@Override
	@Nullable
	public abstract T instantiate (Class<?> cls);

	@Override
	public abstract JsonElement serialize (T object);

	@Override
	public abstract T deserialize (T instance, JsonElement element);
}
