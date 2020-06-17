package com.polaris.core.config;

/**
* 第三方扩展包接口
* {@link}Polaris_conf_nacos
* {@link}Polaris_conf_zk
* {@link}Polaris_conf_file
*/
public interface ConfHandler {
    
    /**
     * 初始化-第三方扩展包，Polaris_conf_nacos，Polaris_conf_zk，Polaris_conf_file等等
     * @return 
     * @Exception 
     * @since 
     */
    default void init() {}
    
    /**
     * 监听指定文件-第三方扩展包，Polaris_conf_nacos，Polaris_conf_zk，Polaris_conf_file等等
     * @param  fileName 被监听的文件
     * @param  group 
     * @param  listener 
     * @return 
     * @Exception 
     * @since 
     */
	default void listen(String fileName, String group, ConfHandlerListener listener) {}
	
    /**
     * 获取文件内容-第三方扩展包，Polaris_conf_nacos，Polaris_conf_zk，Polaris_conf_file等等
     * @param  fileName 指定的文件
     * @param  group 
     * @return String 文件内容
     * @Exception 
     * @since 
     */
	default String get(String fileName, String group) {return null;}
	
	/**
     * 获取+监听文件内容-第三方扩展包，Polaris_conf_nacos，Polaris_conf_zk，Polaris_conf_file等等
     * @param  fileName 指定的文件
     * @param  group 
     * @param  listeners 
     * @return String 文件内容
     * @Exception 
     * @since 
     */
    default String getAndListen(String fileName, String group, ConfHandlerListener... listeners) {
        String result = get(fileName,group);
        if (listeners != null) {
            for (ConfHandlerListener listener: listeners) {
                listen(fileName,group,listener);
            }
        }
        return result;
    }
}
