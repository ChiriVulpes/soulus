package yuudaari.soulus.common.util;

public class GeneratorName {

	public static String get (String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		switch (name) {
			case "Woodland_mansion":
				return "Mansion";
			case "Nether_bridge":
				return "Fortress";
			case "Ocean_monument":
				return "Monument";
			case "Scattered_feature":
				return "Temple";
		}
		return name;
	}
}
