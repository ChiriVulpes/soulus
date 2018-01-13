package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;

public class SerializationHandlers {

	public static interface IFieldSerializationHandler<T> {

		JsonElement serialize (T object);
	}

	public static interface IFieldDeserializationHandler<T extends Object> {

		T deserialize (Class<?> expectedType, JsonElement element);
	}

	public static interface IClassSerializationHandler<T> {

		JsonElement serialize (T object);
	}

	public static interface IClassDeserializationHandler<T extends Object> {

		@Nullable
		T instantiate (Class<?> cls);

		T deserialize (T instance, JsonElement element);
	}
}
