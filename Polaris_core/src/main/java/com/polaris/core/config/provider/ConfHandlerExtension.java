package com.polaris.core.config.provider;

import java.util.Properties;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.reader.ConfReaderFactory;

public class ConfHandlerExtension extends ConfHandlerDefault {
    
    //instance-var
    protected ConfigChangeListener[] configChangeListeners;
    protected ConfigChangeNotifier notifier = ConfigChangeNotifier.INSTANCE;
	public ConfHandlerExtension(ConfigChangeListener... configChangeListeners) {
	    super();
	    this.configChangeListeners = configChangeListeners;
	}

	@Override
    public String get(String group, String fileName) {
		if (handler != null) {
		    String contents = super.get(group, fileName);
		    configChangeNotify(group,fileName,contents);
		    return contents;
		}
    	return null;
	}
	
	
	@Override
    public void listen(String group, String fileName,ConfHandlerListener listener) {
		if (handler != null) {
            if (listener != null) {
                super.listen(group, fileName, listener);
            }
			handler.listen(group, fileName, new ConfHandlerListener() {
	            @Override
	            public void receive(String contents) {
	                configChangeNotify(group,fileName,contents);
	            }
	        });
		}
	}
	
    private void configChangeNotify(String group,String fileName, String contents) {
        Properties oldProperties = null;

        //copy 
        Properties tempProperties = Config.INSTANCE.getProperties(group, fileName);
        if (tempProperties != null) {
            oldProperties = new Properties();
            oldProperties.putAll(tempProperties);
        }
        Properties newProperties = ConfReaderFactory.get(fileName).getProperties(contents);
        notifier.notify(group, fileName, oldProperties,newProperties,configChangeListeners);
    }

}
