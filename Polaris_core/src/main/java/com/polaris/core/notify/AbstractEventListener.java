package com.polaris.core.notify;

import java.util.EventListener;
import java.util.List;

import com.polaris.core.notify.Event;

public abstract class AbstractEventListener implements EventListener{
    
    public AbstractEventListener() {
        /**
         * automatic register
         */
        EventPublisher.addEventListener(this);
    }

    /**
     * 感兴趣的事件列表
     *
     * @return event list
     */
    abstract public List<Class<? extends Event>> interest();

    /**
     * 处理事件
     *
     * @param event event
     */
    abstract public void onEvent(Event event);
}
