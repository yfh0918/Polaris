package com.polaris.core.notify;

public abstract class SingleEventListener<E extends Event> extends AbstractEventListener{
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
