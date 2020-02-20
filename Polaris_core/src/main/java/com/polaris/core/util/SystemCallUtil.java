package com.polaris.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;

public abstract class SystemCallUtil {

	private static Map<String, String> encryptMap = new ConcurrentHashMap<>();
	public static String value() {
    	String key = ConfClient.get(Constant.SYSTEM_CALL_ENCRYPT_KEY,Constant.SYSTEM_CALL_ENCRYPT_KEY_DEFAULT);
    	String startwith = ConfClient.get(Constant.SYSTEM_CALL_START_WITH,Constant.SYSTEM_CALL_START_WITH_DEFAULT);
    	String strChars = ConfClient.get(Constant.SYSTEM_CALL_ENCRYPT_VALUE,Constant.SYSTEM_CALL_ENCRYPT_VALUE_DEFAULT);
    	String systemKey = key + startwith + strChars;
    	String value = encryptMap.get(systemKey);
    	if (StringUtil.isNotEmpty(value)) {
    		return value;
    	}
        EncryptUtil en = EncryptUtil.getInstance(key);
        try {
			value = en.encrypt(startwith, strChars);
			encryptMap.put(systemKey, value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static String key() {
		return ConfClient.get(Constant.SYSTEM_CALL_HEADER_KEY, Constant.SYSTEM_CALL_HEADER_KEY_DEFAULT);
	}
	
	public static boolean verify(String sourceValue) {
		if (StringUtil.isEmpty(sourceValue)) {
			return false;
		}
		String key = ConfClient.get(Constant.SYSTEM_CALL_ENCRYPT_KEY,Constant.SYSTEM_CALL_ENCRYPT_KEY_DEFAULT);
    	String startwith = ConfClient.get(Constant.SYSTEM_CALL_START_WITH,Constant.SYSTEM_CALL_START_WITH_DEFAULT);
    	String strChars = ConfClient.get(Constant.SYSTEM_CALL_ENCRYPT_VALUE,Constant.SYSTEM_CALL_ENCRYPT_VALUE_DEFAULT);
    	String systemKey = key + startwith + strChars;
    	String value = encryptMap.get(systemKey);
    	if (StringUtil.isEmpty(value)) {
    		return sourceValue.equals(value());
    	}
		return sourceValue.equals(value);
	}
}
