package com.polaris.core.config;

import java.util.Collection;
import java.util.Properties;

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
		SYS("project.system.properties",null),//system,目前用不到
		EXT("project.extension.properties",null),//application.properties中定义的extension
	    GBL("project.global.properties","global");//application.properties中定义的global
        private String propertyType;
        private String group;
	    Type(String propertyType, String group) {
	        this.propertyType = propertyType;
	        this.group = group;
	    }
        public String getPropertyType() {
            return propertyType;
        }
        public String getGroup() {
            return group == null?ConfClient.getAppName():group;
        }
	}
	
	Type getType();
	default Collection<Properties> getProperties() {return null;}
	default Properties getProperties(String file) {return null;}
	default void put(String file, Properties properties) {};
	default void put(String file, Object key, Object value) {};
	default boolean contain(String file, Object key){return false;}
	default boolean contain(Object key) {return false;}
}
