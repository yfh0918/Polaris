package com.polaris.core.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

public class NullLogger extends AbstractLogger{

	public static NullLogger INSTANCE = new NullLogger();
	private NullLogger () {
		super();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4, Object p5) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4, Object p5, Object p6) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4, Object p5, Object p6, Object p7) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4, Object p5, Object p6, Object p7, Object p8) {
		return false;
	}

	@Override
	public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
			Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		return false;
	}

	@Override
	public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
	}

	@Override
	public Level getLevel() {
		return null;
	}

}
