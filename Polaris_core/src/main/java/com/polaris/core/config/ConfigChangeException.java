package com.polaris.core.config;

public class ConfigChangeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ConfigChangeException() {
		super();
	}
	public ConfigChangeException(String message) {
		super(message);
	}
}
