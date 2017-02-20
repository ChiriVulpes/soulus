package com.kallgirl.souls.common.util;

public interface IModObject {
	default void preinit() {}
	default void init() {}
	default void postinit() {}
}
