package com.polaris.core.config.provider;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;

public class ConfHandlerGblProvider extends ConfHandlerAbsProvider {

	private final String GLOBAL = "global";
	public static ConfHandlerGblProvider INSTANCE = new ConfHandlerGblProvider();
	
	private ConfHandlerGblProvider() {
	}
	
	@Override
	public void init(ConfigListener configListener) {
		init(configListener,Type.GBL,Constant.PROJECT_GLOBAL_PROPERTIES);
	}

	@Override
    public boolean getAndListen(String file) {
		return getAndListen(file, GLOBAL, ConfigFactory.get(Type.GBL));
    }
    
	@Override
	public String get(String file) {
		return get(file,GLOBAL);
	}
	
	@Override
	public void listen(String file, ConfHandlerListener listener) {
		listen(file,GLOBAL,listener);
	}
}
