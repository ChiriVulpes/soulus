package yuudaari.soulus.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import yuudaari.soulus.common.util.Logger;

public class Serializer<T> {

	public static interface SerializeOtherHandler {

		public JsonElement serialize (Object obj);
	}

	public static interface DeserializeOtherHandler {

		public Object deserialize (JsonElement json, Object current);
	}

	public Class<T> targetClass;

	/** A camelCase list of primitive fields to serialize/deserialize (will appear as snake_case in json) */
	public List<String> fields = new ArrayList<>();

	public Map<String, Serializer<?>> fieldHandlers = new HashMap<>();
	public Map<String, Serializer<?>> otherHandlers = new HashMap<>();

	public Serializer () {}

	public Serializer (Class<T> cls, String... primitiveFields) {
		this.targetClass = cls;
		this.fields.addAll(Arrays.asList(primitiveFields));
	}

	public Serializer (Class<T> cls, Map<String, Serializer<?>> handlers, String... primitiveFields) {
		this.targetClass = cls;
		this.fields.addAll(Arrays.asList(primitiveFields));
		this.fieldHandlers = handlers;
	}

	public JsonElement serialize (Object from) {
		JsonObject result = new JsonObject();

		// serialize primitive fields
		for (String fieldName : fields) {
			Field field;
			try {
				field = targetClass.getField(fieldName);
			} catch (NoSuchFieldException e) {
				Logger.warn("Field '" + fieldName + "' does not exist in class");
				continue;
			}

			Class<?> type = field.getType();

			if (!isPrimitiveType(type)) {
				Logger.warn("Field '" + fieldName + "' is not a primitive ('" + type
					.getName() + "'), it can't be serialized");
				continue;
			}

			Object value;

			try {
				value = field.get(from);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Logger.warn("Can't get field '" + fieldName + "' from object");
				continue;
			}

			String jsonFieldName = toSnakeCase(fieldName);
			result.add(jsonFieldName, getPrimitive(value));
		}

		// serialize handlers
		for (Map.Entry<String, Serializer<?>> handlerEntry : fieldHandlers.entrySet()) {
			String fieldName = handlerEntry.getKey();
			Serializer<?> handler = handlerEntry.getValue();

			Field field;
			try {
				field = targetClass.getField(fieldName);
			} catch (NoSuchFieldException e) {
				Logger.warn("Field '" + fieldName + "' does not exist in class");
				continue;
			}

			Object value;

			try {
				value = field.get(from);
			} catch (IllegalAccessException e) {
				Logger.warn("Can't get field '" + fieldName + "' from object");
				continue;
			}

			String jsonFieldName = toSnakeCase(fieldName);
			result.add(jsonFieldName, handler.serialize(value));
		}

		// serialize other handlers
		for (Map.Entry<String, Serializer<?>> handlerEntry : otherHandlers.entrySet()) {
			String fieldName = handlerEntry.getKey();
			Serializer<?> handler = handlerEntry.getValue();

			String jsonFieldName = toSnakeCase(fieldName);
			result.add(jsonFieldName, handler.serialize(null));
		}

		return result;
	}

	public T deserialize (JsonElement from) {
		T into = null;
		try {
			into = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Logger.warn("Could not instantiate the class");
		}

		deserialize(from, into);

		return into;
	}

	public Object deserialize (JsonElement from, Object into) {
		Logger.scopes.add(targetClass.getSimpleName());

		if (into != null) {

			if (from == null || !from.isJsonObject()) {
				Logger.warn("Json to deserialize must be an object");
			} else {

				JsonObject json = from.getAsJsonObject();

				// everything's good, start deserializing!

				// deserialize primitive fields
				for (String fieldName : fields) {
					Field field;
					try {
						field = targetClass.getField(fieldName);
					} catch (NoSuchFieldException e) {
						Logger.warn("Field '" + fieldName + "' does not exist in class");
						continue;
					}

					Class<?> type = field.getType();

					String jsonFieldName = toSnakeCase(fieldName);
					JsonElement fieldElement = json.get(jsonFieldName);

					Object deserialized = getAsType(fieldElement, type);

					if (deserialized == null) {
						Logger.warn("The property '" + jsonFieldName + "' can't be null");
						continue;
					}

					try {
						field.set(into, deserialized);
					} catch (IllegalAccessException e) {
						Logger.warn("No access to field '" + fieldName + "'");
						continue;
					}
				}

				// deserialize handlers
				for (Map.Entry<String, Serializer<?>> handlerEntry : fieldHandlers.entrySet()) {
					String fieldName = handlerEntry.getKey();
					Serializer<?> handler = handlerEntry.getValue();

					Field field;
					try {
						field = targetClass.getField(fieldName);
					} catch (NoSuchFieldException e) {
						Logger.warn("Field '" + fieldName + "' does not exist in class");
						continue;
					}

					Object value;

					try {
						value = field.get(into);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						Logger.warn("Can't get field '" + fieldName + "' from object");
						continue;
					}

					String jsonFieldName = toSnakeCase(fieldName);
					JsonElement fieldElement = json.get(jsonFieldName);

					Object deserialized = handler.deserialize(fieldElement, value);

					try {
						field.set(into, deserialized);
					} catch (IllegalAccessException e) {
						Logger.warn("No access to field '" + fieldName + "'");
						continue;
					}
				}

				// deserialize other handlers
				for (Map.Entry<String, Serializer<?>> handlerEntry : otherHandlers.entrySet()) {
					String fieldName = handlerEntry.getKey();
					Serializer<?> handler = handlerEntry.getValue();

					String jsonFieldName = toSnakeCase(fieldName);
					JsonElement fieldElement = json.get(jsonFieldName);

					handler.deserialize(fieldElement, null);
				}
			}

		}

		Logger.scopes.pop();

		return into;
	}

	private Object getAsType (JsonElement jsonElement, Class<?> type) {
		if (jsonElement != null) {

			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

				if (primitive.isNumber()) {
					if (type == Integer.class || type == int.class)
						return primitive.getAsInt();
					else if (type == Short.class || type == short.class)
						return primitive.getAsShort();
					else if (type == Byte.class || type == byte.class)
						return primitive.getAsByte();
					else if (type == Float.class || type == float.class)
						return primitive.getAsFloat();
					else if (type == Double.class || type == double.class || type == Number.class)
						return primitive.getAsDouble();

				} else {
					if (type == String.class) {
						if (primitive.isString())
							return primitive.getAsString();

					} else if (type == Boolean.class || type == boolean.class) {
						if (primitive.isBoolean())
							return primitive.getAsBoolean();

					}
				}
			}

		}

		return null;
	}

	private static JsonPrimitive getPrimitive (Object val) {
		if (val instanceof String)
			return new JsonPrimitive((String) val);
		else if (val instanceof Boolean)
			return new JsonPrimitive((Boolean) val);
		else if (val instanceof Number)
			return new JsonPrimitive((Number) val);
		else
			return null;
	}

	private static boolean isPrimitiveType (Class<?> type) {
		return type == String.class || type == Boolean.class || type == boolean.class || Number.class
			.isAssignableFrom(type) || type == int.class || type == short.class || type == byte.class || type == double.class || type == float.class;
	}

	private static final Pattern camelCaseRegex = Pattern.compile("[A-Z]");

	private static String toSnakeCase (String camelCase) {
		return stringReplaceAll(camelCase, camelCaseRegex, (Matcher matcher) -> {
			String match = matcher.group();
			return "_" + match.toLowerCase();
		});
	}

	/*
	private static final Pattern snakeCaseRegex = Pattern.compile("_[A-Za-z]");
	
	private static String toCamelCase(String snakeCase) {
		return stringReplaceAll(snakeCase, snakeCaseRegex, (Matcher matcher) -> {
			String match = matcher.group();
			return match.substring(1).toUpperCase();
		});
	}
	*/

	private static interface ReplaceHandler {

		public String handle (Matcher matcher);
	}

	private static String stringReplaceAll (String string, Pattern pattern, ReplaceHandler handler) {
		Matcher matcher = pattern.matcher(string);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, handler.handle(matcher));
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}
