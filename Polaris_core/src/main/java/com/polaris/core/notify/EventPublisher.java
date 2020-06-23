package com.polaris.core.notify;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    private static final CopyOnWriteArrayList<EventEntry> LISTENER_HUB = new CopyOnWriteArrayList<EventEntry>();

    /**
     * fire event, notify listeners. - sync
     */
    public static void fireEvent(Event event) {
        checkNotNull(event);
        for (AbstractEventListener listener : getEntry(event.getClass()).listeners) {
            if (listener.getExecutor() == null) {
                fireEvent0(event,listener);
            } else {
                listener.getExecutor().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            fireEvent0(event,listener);
                        }
                    }
                ); 
            }
            
        }
    }
    
    private static void fireEvent0(Event event, AbstractEventListener listener) {
        try {
            if (listener instanceof MultiEventListener) {
                ((MultiEventListener)listener).onEvent(event);
            } else {
                ((SingleEventListener)listener).onEvent(event);
            }
            
        } catch (Exception e) {
            log.error(e.toString(), e);
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
        if (listener instanceof MultiEventListener) {
            addEventListener0((MultiEventListener)listener);
        } else if (listener instanceof SingleEventListener) {
            addEventListener1((SingleEventListener)listener);
        }
    }
    /**
     * add multi event listener
     */
    private static void addEventListener0(MultiEventListener listener) {
        for (Class<? extends Event> type : listener.interest()) {
            getEntry(type).listeners.addIfAbsent(listener);
        }
    }

    /**
     * add single event listener
     */
    private static <E extends Event> void addEventListener1(SingleEventListener<E> listener) {
        Type type = listener.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] args = parameterizedType.getActualTypeArguments();
            if (args != null) {
                for (Type arg : args) {
                    try {
                        getEntry((Class<E>)arg).listeners.addIfAbsent(listener);
                        break;
                    } catch (Exception ex) {
                        continue;
                    }
                }
            }
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
