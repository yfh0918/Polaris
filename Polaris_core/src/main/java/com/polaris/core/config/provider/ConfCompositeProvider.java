package com.polaris.core.config.provider;

import com.polaris.core.config.ConfSystemHandler;
import com.polaris.core.config.Config;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;


public class ConfCompositeProvider extends ConfHandlerProvider {

	/**
     * 单实例
     */
    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfSystemHandler INSTANCE_SYSTEM = ConfSystemHandler.INSTANCE;
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private ConfCompositeProvider() {}
    
    @Override
    public void init() {
    	INSTANCE_SYSTEM.init(this);
    	INSTANCE_ENDPOINT.init(this);
    	super.init();
    }

	@Override
	public void put(Config config, String key, String value) {
		INSTANCE_ENDPOINT.filter(key, value);
		super.put(config, key, value);
	}
	
	@Override
	public void listenForPut(Config config, String key, String value) {
		SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(config.get());//监听配置
		super.listenForPut(config, key, value);
	}
}
