package com.polaris.core.config.provider;

import com.polaris.core.config.Config;
import com.polaris.core.config.value.SpringAutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;


public class ConfCompositeProvider extends ConfHandlerProvider {

    public static final ConfCompositeProvider INSTANCE = new ConfCompositeProvider();
    private static final ConfSystemHandlerProvider INSTANCE_SYSTEM = ConfSystemHandlerProvider.INSTANCE;
    private static final ConfEndPointProvider INSTANCE_ENDPOINT = ConfEndPointProvider.INSTANCE;
    private ConfCompositeProvider() {}
    
    @Override
    public void init() {
    	INSTANCE_SYSTEM.init();
    	INSTANCE_ENDPOINT.init();
    	super.init();
    }

	@Override
	public void put(Config config, String key, String value) {
		INSTANCE_ENDPOINT.filter(key, value);
		super.put(config, key, value);
	}
	
	@Override
	public void listenForPut(Config config, String key, String value) {
		SpringUtil.getBean(SpringAutoUpdateConfigChangeListener.class).onChange(config.get());//监听配置
		super.listenForPut(config, key, value);
	}
}
