package com.polaris.core.config;

import com.polaris.core.Constant;
import com.polaris.core.util.EncryptUtil;
import com.polaris.core.util.StringUtil;

public class ConfHandlerSupport {

	public static String getGroup(String type) {
		if (Config.GLOBAL.equals(type)) {
			return type;
		}
		return ConfClient.getAppName();
	}
	
	/**
	* 获取扩展配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getProperties(String type) {
		String files = null;
		if (type.equals(Config.EXTEND)) {
			files = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(),Constant.PROJECT_EXTENSION_PROPERTIES);
		} else  if (type.equals(Config.GLOBAL)) {
			files = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(),Constant.PROJECT_GLOBAL_PROPERTIES);
		}
		if (StringUtil.isEmpty(files)) {
			return null;
		}
		return files.split(",");
	}
	
	/**
	* 获取KV对
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getKeyValue(String line) {
		if (StringUtil.isNotEmpty(line)) {
			String[] keyvalue = line.split("=");
			if (keyvalue.length == 0) {
				return new String[] {"",""};
			}
			if (keyvalue.length == 1) {
				return new String[] {keyvalue[0].trim(),""};
			}
			String value = "";
			for (int index = 0; index < keyvalue.length; index++) {
				if (index != 0) {
					if (StringUtil.isEmpty(value)) {
						value = keyvalue[index].trim();
					} else {
						value = value + "=" + keyvalue[index].trim();
					}
				}
			}
			return new String[] {keyvalue[0].trim(),value};
		}
		return null;
	}
	
	public static String getDecryptValue(String propVal) {
		//解密操作
		try {
			EncryptUtil encrypt = EncryptUtil.getInstance();
			propVal = encrypt.decrypt(EncryptUtil.START_WITH, propVal);
		} catch (Exception ex) {
			//nothing
		}
		return propVal;
	}
	

}
