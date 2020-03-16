package com.polaris.core.config.provider;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config;
import com.polaris.core.config.ConfigException;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.util.StringUtil;

public class ConfHandlerGlobalProvider extends ConfHandlerAbsProvider {

	public static ConfHandlerGlobalProvider INSTANCE = new ConfHandlerGlobalProvider();
	
	private ConfHandlerGlobalProvider() {
	}
	
	@Override
	public void init(ConfigListener configListener) {
		
		super.init(configListener);
		
		//get target files
		String files = ConfigFactory.get(Config.SYSTEM).getProperties(Config.SYSTEM).getProperty(Constant.PROJECT_GLOBAL_PROPERTIES);
		if (StringUtil.isEmpty(files)) {
			return;
		}
		String[] fileArray = files.split(",");
		
		//target files loop
		for (String file : fileArray) {
			if (!init(file)) {
				throw new ConfigException("type:ext file:"+file+" is not exsit");
			}
		}
	}

	@Override
    public boolean init(String file) {
    	
		//get config
		Config config = ConfigFactory.get(Config.GLOBAL);
		
		//result
		return init(file, Config.GLOBAL, config);
    }
    
	@Override
	public String get(String file) {
		return get(file,Config.GLOBAL);
	}
	
	@Override
	public void listen(String file, ConfHandlerListener listener) {
		listen(file,Config.GLOBAL,listener);
	}

}
