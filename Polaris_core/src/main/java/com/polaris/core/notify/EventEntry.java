package com.polaris.core.notify;

import java.util.EventListener;
import java.util.concurrent.CopyOnWriteArrayList;

class EventEntry {
    final Class<? extends Event> eventType;
    final CopyOnWriteArrayList<EventListener> listeners;

    EventEntry(Class<? extends Event> type) {
        eventType = type;
        listeners = new CopyOnWriteArrayList<EventListener>();
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
}
