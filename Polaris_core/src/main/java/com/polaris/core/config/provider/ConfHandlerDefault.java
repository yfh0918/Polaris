package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.exception.ConfigException;

@SuppressWarnings("rawtypes")
public class ConfHandlerDefault implements ConfHandler{
    protected static final ServiceLoader<ConfHandler> handlerLoader = ServiceLoader.load(ConfHandler.class);
    private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
    protected static ConfHandler handler;
    public ConfHandlerDefault() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        List<OrderWrapper> configHandlerList = new ArrayList<OrderWrapper>();
        for (ConfHandler configHandler : handlerLoader) {
            OrderWrapper.insertSorted(configHandlerList, configHandler);
        }
        if (configHandlerList.size() > 0) {
            handler = (ConfHandler)configHandlerList.get(0).getHandler();
        }
        if (handler == null) {
            throw new ConfigException("Excepiton caused by ConfHandler is null");
        }
    }
    
    @Override
    public String get(String group, String fileName) {
        if (handler != null) {
            return handler.get(group, fileName);
        }
        return null;
    }
    
    
    @Override
    public void listen(String group, String fileName,ConfHandlerListener listener) {
        if (handler != null) {
            handler.listen(group, fileName, listener);
        }
    }
}
