package com.polaris.core.component;

/**
 * The lifecycle interface for generic components.
 * <br>
 * Classes implementing this interface have a defined life cycle
 * defined by the methods of this interface.
 */
public interface LifeCycle {

    /**
     * Starts the component.
     *
     * @throws Exception If the component fails to start
     * @see #isStarted()
     * @see #stop()
     * @see #isFailed()
     */
    void start() throws Exception;
    /**
     * Stops the component.
     * The component may wait for current activities to complete
     * normally, but it can be interrupted.
     *
     * @throws Exception If the component fails to stop
     * @see #isStopped()
     * @see #start()
     * @see #isFailed()
     */
    void stop() throws Exception;

}
