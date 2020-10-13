package com.polaris.core.config;

import java.util.Collection;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.util.StringUtil;

/**
* properties文件，yaml文件的对外接口暴露
* {@link}ConfHandlerComposite
* {@link}ConfHandlerProxy
*/
public interface Config {
	
	public enum Opt {
		ADD,//add
		UPD,//update
	    DEL;//delete
	}
	
	public enum Type {
		SYS(),
		EXT();
	}
	
	public static String merge(String group, String fileName) {
        return group + Constant.COLON + fileName;
    }
    public static String group() {
        if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
            return ConfClient.getGroup();
        }
        return ConfClient.getAppName();
    }
	
	default Collection<Properties> getProperties() {return null;}
	default Properties getProperties(String file) {return null;}
	default void put(String file, Properties properties) {};
	default void put(String file, Object key, Object value) {};
	default boolean contain(String file, Object key){return false;}
	default boolean contain(Object key) {return false;}
}
