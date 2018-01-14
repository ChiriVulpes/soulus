package yuudaari.soulus.common.util.serializer;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SerializationHandlers {

	public static interface IFieldSerializationHandler<T> {

		JsonElement serialize (Class<?> objectType, T object);
	}

	public static interface IFieldDeserializationHandler<T extends Object> {

		T deserialize (Class<?> expectedType, JsonElement element);
	}

	public static interface IClassSerializationHandler<T> {

		void serialize (T object, JsonObject into);
	}

	public static interface IClassDeserializationHandler<T extends Object> {

		@Nullable
		T instantiate (Class<?> cls);

		T deserialize (@Nullable T instance, JsonElement element);
	}
}
