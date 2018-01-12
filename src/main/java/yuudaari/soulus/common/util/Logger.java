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

	private static String getScopes () {
		return scopes.size() == 0 ? "" : "[" + String.join(" | ", scopes) + "] ";
	}

	public static void inScope (String scope, ScopeHandler handler) {
		scopes.push(scope);
		handler.handle();
		scopes.pop();
	}

	public static void info (String message) {
		LOGGER.info(getScopes() + message);
	}

	public static void info (String scope, String message) {
		scopes.push(scope);
		LOGGER.info(getScopes() + message);
		scopes.pop();
	}

	public static void warn (String message) {
		LOGGER.warn(getScopes() + message);
	}

	public static void warn (String scope, String message) {
		scopes.push(scope);
		LOGGER.warn(getScopes() + message);
		scopes.pop();
	}

	public static void error (String message) {
		LOGGER.error(getScopes() + message);
	}

	public static void error (String scope, String message) {
		scopes.push(scope);
		LOGGER.error(getScopes() + message);
		scopes.pop();
	}

	public static void error (Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		printWriter.flush();

		String stackTrace = writer.toString();

		error(stackTrace);
	}

}
