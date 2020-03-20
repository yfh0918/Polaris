package com.polaris.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * Event dispatcher
 *
 * @author 
 */
public class EventDispatcher {

    /**
     * add event listener
     */
    static public void addEventListener(AbstractEventListener listener) {
        for (Class<? extends Event> type : listener.interest()) {
            getEntry(type).listeners.addIfAbsent(listener);
        }
    }

    /**
     * fire event, notify listeners. - sync
     */
    static public void fireEvent(Event event) {
    	checkNotNull(event);

        for (AbstractEventListener listener : getEntry(event.getClass()).listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }
    
    /**
     * fire event, notify listeners. - async
     */
    static public void fireEvent(Event event,Executor executor) {
    	checkNotNull(event);
        checkNotNull(executor);
        executor.execute(
            new Runnable() {
              @Override
              public void run() {
            	  fireEvent(event);
              }
            }
        );
    }

    /**
     * For only test purpose
     */
    static public void clear() {
        LISTENER_HUB.clear();
    }

    /**
     * get event listener for eventType. Add Entry if not exist.
     */
    static Entry getEntry(Class<? extends Event> eventType) {
        for (; ; ) {
            for (Entry entry : LISTENER_HUB) {
                if (entry.eventType == eventType) {
                    return entry;
                }
            }

            Entry tmp = new Entry(eventType);
            /**
             *  false means already exists
             */
            if (LISTENER_HUB.addIfAbsent(tmp)) {
                return tmp;
            }
        }
    }

    static private class Entry {
        final Class<? extends Event> eventType;
        final CopyOnWriteArrayList<AbstractEventListener> listeners;

        Entry(Class<? extends Event> type) {
            eventType = type;
            listeners = new CopyOnWriteArrayList<AbstractEventListener>();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj || obj.getClass() != getClass()) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            return eventType == ((Entry)obj).eventType;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }

    static private final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    static final CopyOnWriteArrayList<Entry> LISTENER_HUB = new CopyOnWriteArrayList<Entry>();

    public interface Event {
    }

    static public abstract class AbstractEventListener {

        public AbstractEventListener() {
            /**
             * automatic register
             */
            EventDispatcher.addEventListener(this);
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

}
