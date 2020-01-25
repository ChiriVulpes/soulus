package yuudaari.soulus.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import yuudaari.soulus.Soulus;

public class Logger {

	public interface ScopeHandler {

		public void handle ();
	}

	public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Soulus.MODID);

	public static final Stack<String> scopes = new Stack<>();

	private static final String MAGIC_DISABLED = "<disabled>";

	private static boolean enabled = true;

	public static void disable () {
		scopes.push(MAGIC_DISABLED);
		enabled = false;
	}

	public static void enable () {
		if (scopes.peek() == MAGIC_DISABLED)
			scopes.pop();
		enabled = !scopes.contains(MAGIC_DISABLED);
	}

	public static boolean enabled () {
		return enabled;
	}

	private static String getScopes () {
		return scopes.size() == 0 ? "" : "[" + String.join(" | ", scopes) + "] ";
	}

	public static void inScope (String scope, ScopeHandler handler) {
		scopes.push(scope);
		handler.handle();
		scopes.pop();
	}

	public static void info (String message) {
		if (enabled())
			LOGGER.info(getScopes() + message);
	}

	public static void info (String scope, String message) {
		scopes.push(scope);
		if (enabled())
			LOGGER.info(getScopes() + message);
		scopes.pop();
	}

	public static void warn (String message) {
		if (enabled())
			LOGGER.warn(getScopes() + message);
	}

	public static void warn (String scope, String message) {
		scopes.push(scope);
		if (enabled())
			LOGGER.warn(getScopes() + message);
		scopes.pop();
	}

	public static void error (String message) {
		if (enabled())
			LOGGER.error(getScopes() + message);
	}

	public static void error (String scope, String message) {
		scopes.push(scope);
		if (enabled())
			LOGGER.error(getScopes() + message);
		scopes.pop();
	}

	public static void error (Exception e) {
		if (!enabled())
			return;

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		printWriter.flush();

		String stackTrace = writer.toString();

		error(stackTrace);
	}

}
