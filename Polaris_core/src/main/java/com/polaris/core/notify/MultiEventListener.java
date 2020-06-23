package com.polaris.core.notify;

import java.util.List;

public abstract class MultiEventListener extends AbstractEventListener{
    
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
