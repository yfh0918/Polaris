package com.polaris.core.notify;

import java.util.concurrent.CopyOnWriteArrayList;

class EventEntry {

    private static final CopyOnWriteArrayList<EventEntry> LISTENER_HUB = new CopyOnWriteArrayList<EventEntry>();

    final Class<? extends Event> eventType;
    final CopyOnWriteArrayList<AbstractEventListener> listeners;

    EventEntry(Class<? extends Event> type) {
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
        return eventType == ((EventEntry)obj).eventType;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * get event listener for eventType. Add Entry if not exist.
     */
    public static EventEntry get(Class<? extends Event> eventType) {
        for (; ; ) {
            for (EventEntry entry : LISTENER_HUB) {
                if (entry.eventType == eventType) {
                    return entry;
                }
            }

            EventEntry tmp = new EventEntry(eventType);
            if (LISTENER_HUB.addIfAbsent(tmp)) {
                return tmp;
            }
        }
    }
}
