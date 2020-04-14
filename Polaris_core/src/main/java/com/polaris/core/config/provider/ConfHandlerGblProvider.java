package com.polaris.core.config.provider;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigException;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.util.StringUtil;

public class ConfHandlerGblProvider extends ConfHandlerAbsProvider {

	private final String GLOBAL = "global";
	public static ConfHandlerGblProvider INSTANCE = new ConfHandlerGblProvider();
	
	private ConfHandlerGblProvider() {
	}
	
	@Override
	public void init(ConfigListener configListener) {
		
		super.init(configListener);
		
		//get target files
		String files = ConfigFactory.get(Type.SYS).getProperties(Type.SYS.name()).getProperty(Constant.PROJECT_GLOBAL_PROPERTIES);
		if (StringUtil.isEmpty(files)) {
			return;
		}
		String[] fileArray = files.split(",");
		
		//target files loop
		for (String file : fileArray) {
			if (!getAndListen(file)) {
				throw new ConfigException("type:ext file:"+file+" is not exsit");
			}
		}
	}

	@Override
    public boolean getAndListen(String file) {
    	
		//get config
		Config config = ConfigFactory.get(Type.GBL);
		
		//result
		return getAndListen(file, GLOBAL, config);
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
