package yuudaari.souls.common.util;

public interface IModObject {
	default void preinit() {
	}

	default void init() {
	}

	default void postinit() {
	}

	abstract String getName();

	abstract void setName(String name);
}