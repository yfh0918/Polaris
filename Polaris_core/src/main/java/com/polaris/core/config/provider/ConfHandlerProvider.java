package com.polaris.core.config.provider;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfigListener;

public interface ConfHandlerProvider {
	default void init(ConfigListener configListener) {}
    default boolean init(String file) {return true;}
    default String get(String file) {return null;}
    default void listen(String file, ConfHandlerListener listener) {}
}
