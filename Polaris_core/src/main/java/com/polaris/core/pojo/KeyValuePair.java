package com.polaris.core.pojo;

import static com.google.common.base.Preconditions.checkNotNull;

public class KeyValuePair {

	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		checkNotNull(key);
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		checkNotNull(value);
		this.value = value;
	}
	public KeyValuePair(String key, String value) {
		setKey(key);
		setValue(value);
	}
	
	/**
	 * Creates a new {@link Pair} for the given elements.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public static KeyValuePair of(String key, String value) {
		return new KeyValuePair(key, value);
	}
	
	@Override
	public String toString() {
		return "key="+key+",value="+value;
	}
	
	@Override
	public boolean equals(Object pair) {
		if (!(pair instanceof KeyValuePair) || pair == null) {
			return false;
		}
		return key.equals(((KeyValuePair)pair).getKey()) &&
			   value.equals(((KeyValuePair)pair).getValue());
	}
}
