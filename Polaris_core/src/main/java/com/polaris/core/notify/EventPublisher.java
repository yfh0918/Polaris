package com.polaris.core.notify;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    private static final CopyOnWriteArrayList<EventEntry> LISTENER_HUB = new CopyOnWriteArrayList<EventEntry>();

    /**
     * fire event, notify listeners. - sync
     */
    public static void fireEvent(Event event) {
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
    public static void fireEvent(Event event,Executor executor) {
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
     * add event listener
     */
    public static void addEventListener(AbstractEventListener listener) {
        for (Class<? extends Event> type : listener.interest()) {
            getEntry(type).listeners.addIfAbsent(listener);
        }
    }
    
    /**
     * get event listener for eventType. Add Entry if not exist.
     */
    private static EventEntry getEntry(Class<? extends Event> eventType) {
        for (; ; ) {
            for (EventEntry entry : LISTENER_HUB) {
                if (entry.eventType == eventType) {
                    return entry;
                }
            }

            EventEntry tmp = new EventEntry(eventType);
            /**
             *  false means already exists
             */
            if (LISTENER_HUB.addIfAbsent(tmp)) {
                return tmp;
            }
        }
    }
    
    
}
