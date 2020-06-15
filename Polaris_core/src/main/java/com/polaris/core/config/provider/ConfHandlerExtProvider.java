package com.polaris.core.config.provider;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;

public class ConfHandlerExtProvider extends ConfHandlerAbsProvider {

	public static ConfHandlerExtProvider INSTANCE = new ConfHandlerExtProvider();
	
	private ConfHandlerExtProvider() {
	}
	
	@Override
	public void init(ConfigListener configListener) {
		init(configListener,Type.EXT,Constant.PROJECT_EXTENSION_PROPERTIES);
	}

	@Override
    public boolean getAndListen(String file) {
		return getAndListen(file, ConfClient.getAppName(), ConfigFactory.get(Type.EXT));
    }
    
	@Override
	public String get(String file) {
		return get(file,ConfClient.getAppName());
	}
	
	@Override
	public void listen(String file, ConfHandlerListener listener) {
		listen(file,ConfClient.getAppName(),listener);
	}

}
