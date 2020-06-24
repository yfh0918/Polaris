package com.polaris.core.notify;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.Executor;

import com.polaris.core.util.ClassUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EventPublisher {
    
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
     * fire event, notify listeners. - sync
     */
    public static void fireEvent(Event event) {
        checkNotNull(event);
        for (AbstractEventListener listener : EventEntry.get(event.getClass()).listeners) {
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

    /**
     * fire event, notify listeners
     */
    private static void fireEvent0(Event event, AbstractEventListener listener) {
        if (listener instanceof MultiEventListener) {
            ((MultiEventListener)listener).onEvent(event);
        } else if (listener instanceof SingleEventListener) {
            ((SingleEventListener)listener).onEvent(event);
        }
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
            EventEntry.get(type).listeners.addIfAbsent(listener);
        }
    }

    /**
     * add single event listener
     */
    private static <E extends Event> void addEventListener1(SingleEventListener<E> listener) {
        Set<Type> typeSet = ClassUtil.getAllSuperTypes(listener.getClass());
        for (Type type : typeSet) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] args = parameterizedType.getActualTypeArguments();
                if (args != null) {
                    for (Type arg : args) {
                        try {
                            EventEntry.get((Class<E>)arg).listeners.addIfAbsent(listener);
                            return;
                        } catch (Exception ex) {
                            continue;
                        }
                    }
                }
            } 
        }
    }
}
