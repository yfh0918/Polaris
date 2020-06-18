package com.polaris.core.notify;

import java.util.EventListener;

public abstract class SingleEventListener<E extends Event> implements EventListener{
    public SingleEventListener() {
        /**
         * automatic register
         */
        EventPublisher.addEventListener(this);
    }
    /**
     * 处理事件
     *
     * @param event event
     */
    abstract public void onEvent(E event);
}
