package com.kallgirl.souls.common;

public interface IModObject {
	default void preinit() {}
	default void init() {}
	default void postinit() {}
}
