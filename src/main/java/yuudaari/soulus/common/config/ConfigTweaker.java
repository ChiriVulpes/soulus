package yuudaari.soulus.common.config;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.compat.GameStages;
import yuudaari.soulus.common.util.JSON;
import yuudaari.soulus.common.util.Logger;

public class ConfigTweaker {

	public static JsonObject applyTweaks (final String workingDirectory, final JsonObject base, final JsonArray tweaks) {

		for (final JsonElement tweak : tweaks) {
			if (!tweak.isJsonObject()) {
				Logger.warn("Tweaks must be a valid Json Object");
				continue;
			}

			final JsonElement conditions = tweak.getAsJsonObject().get("conditions");
			if (conditions == null || (!conditions.isJsonArray() && !conditions.isJsonObject())) {
				Logger.warn("Tweaks must have a 'conditions' property.");
				continue;
			}

			if (!conditionsMatch(conditions, null)) continue;

			applyTweak(workingDirectory, base, tweak.getAsJsonObject());
		}

		return base;
	}

	private static boolean conditionsMatch (final JsonElement conditions, final String strategy) {
		if (conditions.isJsonArray())
			return conditionListMatches(conditions.getAsJsonArray(), strategy == null ? "and" : strategy);

		return conditionMatches(conditions.getAsJsonObject());
	}

	private static boolean conditionListMatches (final JsonArray conditions, final String strategy) {
		for (final JsonElement condition : conditions) {
			boolean matches = false;

			if (!condition.isJsonObject())
				Logger.warn("Tweak conditions must be Json Objects.");
			else
				matches = conditionMatches(condition.getAsJsonObject());

			if (strategy.equalsIgnoreCase("and") && !matches)
				return false;

			if (strategy.equalsIgnoreCase("or") && matches)
				return true;
		}

		return strategy.equalsIgnoreCase("and"); // "and" strategy will get here expecting "true", "or" will expect "false"
	}

	private static boolean conditionMatches (final JsonObject condition) {
		final JsonElement conditionTypeJson = condition.get("type");
		if (conditionTypeJson == null || !conditionTypeJson.isJsonPrimitive() || !conditionTypeJson.getAsJsonPrimitive().isString()) {
			return conditionParentMatches(condition);
		}

		final String conditionType = conditionTypeJson.getAsString();
		if (conditionType.equalsIgnoreCase("gamestage")) {
			return GameStages.tweakConditionMatches(condition);
		}

		Logger.warn("Tweak condition type '" + conditionType + "' is unknown.");
		return false;
	}

	private static boolean conditionParentMatches (final JsonObject conditionParent) {
		final JsonElement and = conditionParent.get("and");
		if (and != null && (and.isJsonArray() || and.isJsonObject()))
			return conditionsMatch(and, "and");

		final JsonElement or = conditionParent.get("or");
		if (or != null && (or.isJsonArray() || or.isJsonObject()))
			return conditionsMatch(or, "or");

		final JsonElement not = conditionParent.get("not");
		if (not != null && (not.isJsonArray() || not.isJsonObject()))
			return !conditionsMatch(or, null);

		Logger.warn("Tweak conditions must have a 'type' property.");
		return false;
	}

	private static void applyTweak (final String workingDirectory, final JsonObject base, final JsonObject tweak) {
		JsonElement insertStrategyJson = tweak.get("insert_strategy");
		if (insertStrategyJson == null || !insertStrategyJson.isJsonPrimitive() || !insertStrategyJson.getAsJsonPrimitive().isString()) {
			Logger.warn("Tweaks must have an 'insert_strategy' property of 'replace' or 'merge'.");
			return;
		}

		JsonElement insertPathJson = tweak.get("insert_path");
		if (insertPathJson == null)
			insertPathJson = new JsonArray();

		if (!insertPathJson.isJsonArray()) {
			Logger.warn("Tweak 'insert_path' must be an array containing only string or integer values.");
			return;
		}

		final JsonArray insertPath = insertPathJson.getAsJsonArray();

		for (final JsonElement insertPathSegment : insertPath) {
			if (!insertPathSegment.isJsonPrimitive() || (!insertPathSegment.getAsJsonPrimitive().isString() && !insertPathSegment.getAsJsonPrimitive().isNumber())) {
				Logger.warn("Tweak 'insert_path' must be an array containing only string or integer values.");
				return;
			}
		}

		if (!tweak.has("from") && !tweak.has("data")) {
			Logger.warn("Tweaks must have a 'from' or 'data' property. Path: " + getPathString(insertPath));
			return;
		}

		JsonElement tweakData;
		try {
			tweakData = getTweakData(workingDirectory, tweak);
		} catch (final Exception e) {
			Logger.error(e);
			Logger.warn("Unable to load tweak data. Path: " + getPathString(insertPath));
			return;
		}

		final String insertStrategy = insertStrategyJson.getAsString();

		try {
			if (insertStrategy.equalsIgnoreCase("replace"))
				applyTweakReplace(base, tweakData, insertPath);

			else if (insertStrategy.equalsIgnoreCase("merge"))
				applyTweakMerge(base, tweakData, insertPath);

		} catch (final Exception e) {
			Logger.error(e);
		}

	}

	private static JsonElement getTweakData (final String workingDirectory, final JsonObject tweak) {
		final JsonElement fromJson = tweak.get("from");
		if (fromJson != null && fromJson.isJsonPrimitive() && fromJson.getAsJsonPrimitive().isString()) {
			String path = fromJson.getAsString();
			if (!path.endsWith(".json")) path += ".json";
			return Soulus.config.getConfigFileJson(workingDirectory + "/" + path, false, false);
		}

		return tweak.get("data");
	}

	private static void applyTweakReplace (final JsonObject base, final JsonElement tweakData, final JsonArray insertPath) {

		final JsonPrimitive lastKey = insertPath.remove(insertPath.size() - 1).getAsJsonPrimitive();
		final JsonElement element = followJsonPath(base, insertPath);

		if (element.isJsonObject() && lastKey.isString()) {
			element.getAsJsonObject().add(lastKey.getAsString(), tweakData);

		} else if (element.isJsonArray() && lastKey.isNumber()) {
			element.getAsJsonArray().set(lastKey.getAsInt(), tweakData);

		} else {
			Logger.warn("Unable apply tweak as path does not exist. Path: " + getPathString(insertPath));
			return;
		}
	}

	private static void applyTweakMerge (final JsonObject base, final JsonElement tweakData, final JsonArray insertPath) {
		final JsonElement element = followJsonPath(base, insertPath);

		if ((element.isJsonObject() && !tweakData.isJsonObject()) || (element.isJsonArray() && !tweakData.isJsonArray())) {
			Logger.warn("Unable apply merge tweak as the merging data is not the same type as the type to be merged into. Path: " + getPathString(insertPath));
			return;
		}

		JSON.mergeInto(element, tweakData);
	}

	private static JsonElement followJsonPath (JsonElement element, final JsonArray path) {
		for (final JsonElement pathSegmentJson : path) {
			final JsonPrimitive pathSegmentPrimitive = pathSegmentJson.getAsJsonPrimitive();
			if (element.isJsonObject() && pathSegmentPrimitive.isString()) {
				element = element.getAsJsonObject().get(pathSegmentPrimitive.getAsString());

			} else if (element.isJsonArray() && pathSegmentPrimitive.isNumber()) {
				element = element.getAsJsonArray().get(pathSegmentPrimitive.getAsInt());

			} else {
				throw new IllegalArgumentException("Unable apply tweak as path does not exist. Path: " + getPathString(path));
			}
		}

		return element;
	}

	private static String getPathString (final JsonArray path) {
		return Streams.stream(path)
			.map(segment -> segment.getAsString())
			.collect(Collectors.joining("."));
	}

}
