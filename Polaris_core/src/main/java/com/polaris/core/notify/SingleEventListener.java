package com.polaris.core.notify;

public abstract class SingleEventListener<E extends Event> extends AbstractEventListener{
    /**
     * 处理事件
     *
     * @param event event
     */
    abstract public void onEvent(E event);
}
