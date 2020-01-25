package yuudaari.soulus.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	public static String firstMatch (final String str, final String regex) {
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		return matcher.find() ? matcher.group() : null;
	}
}
