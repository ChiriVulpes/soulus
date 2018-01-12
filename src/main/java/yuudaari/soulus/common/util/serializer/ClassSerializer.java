package yuudaari.soulus.common.util.serializer;

import com.google.gson.JsonElement;

public class ClassSerializer extends Serializer<Class<?>> {

	@Override
	public JsonElement serialize (Class<?> object) {
		return null;
	}

	@Override
	public Class<?> deserialize (Class<?> requestedType, JsonElement element) {
		return null;
	}
}
