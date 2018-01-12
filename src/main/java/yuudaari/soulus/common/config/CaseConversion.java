package yuudaari.soulus.common.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseConversion {

	private static final Pattern camelCaseRegex = Pattern.compile("[A-Z]");

	/**
	 * Converts a string from camelCase to snake_case
	 */
	public static String toSnakeCase (final String camelCase) {
		return stringReplaceAll(camelCase, camelCaseRegex, (final Matcher matcher) -> {
			final String match = matcher.group();
			return "_" + match.toLowerCase();
		});
	}

	private static final Pattern snakeCaseRegex = Pattern.compile("_[A-Za-z]");

	/**
	 * Converts a string from snake_case to camelCase
	 */
	public static String toCamelCase (final String snakeCase) {
		return stringReplaceAll(snakeCase, snakeCaseRegex, (final Matcher matcher) -> {
			final String match = matcher.group();
			return match.substring(1).toUpperCase();
		});
	}

	private static interface ReplaceHandler {

		/**
		 * Handle the ocurrence; return what this match should be replaced by.
		 */
		public String handle (final Matcher matcher);
	}

	/**
	 * Replace all ocurrences of a pattern in a string, using a lambda
	 */
	private static String stringReplaceAll (final String string, final Pattern pattern, final ReplaceHandler handler) {
		final Matcher matcher = pattern.matcher(string);
		final StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, handler.handle(matcher));
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}
