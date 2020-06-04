package com.polaris.core.config;

/**
* 作为第三方扩展包的监听参数使用
* {@link}Polaris_conf_nacos
* {@link}Polaris_conf_zk
* {@link}Polaris_conf_file
*/
public interface ConfHandlerListener {
    
    /**
     * 被监听的内容回调方法
     * @param  content 监听到的文件内容
     * @return 
     * @Exception 
     * @since 
     */
    public void receive(String content);
}
