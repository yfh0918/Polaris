package com.polaris.core.log;
import org.apache.logging.log4j.Level;

public interface Log4jCallBack {
	default void call(Level level, String message, Object... args){};
	default void call(Level level, String message, Throwable t) {};
}
