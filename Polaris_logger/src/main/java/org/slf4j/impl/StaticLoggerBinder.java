package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import com.polaris.core.log.ExtendedLoggerContext;


public class StaticLoggerBinder implements LoggerFactoryBinder {

	private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    
    private boolean initialized = false;
    private ExtendedLoggerContext defaultLoggerContext = new ExtendedLoggerContext();
    
    private StaticLoggerBinder() {
    }

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    static void reset() {
        SINGLETON = new StaticLoggerBinder();
        SINGLETON.init();
    }

    static {
        SINGLETON.init();
    }
    
    void init(){
    	initialized = true;
    }
	
	public ILoggerFactory getLoggerFactory() {
        if (!initialized) {
            return defaultLoggerContext;
        }
        return defaultLoggerContext;
	}

	public String getLoggerFactoryClassStr() {
		return this.getClass().getName();
	}

}
