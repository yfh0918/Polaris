package com.polaris.extension.mvc;

import com.polaris.core.adapter.ClassAdapter;
import com.polaris.core.event.EventDispatcher.Event;

public class MVCConfigurerEvent implements Event{

	private Class<?> eventType;
	private Object eventParameter;
	private Object eventResult;
	
	public MVCConfigurerEvent(Class<?> eventType) {
        this.eventType = eventType;
    }
	public MVCConfigurerEvent(Class<?> eventType, Object eventParameter) {
		this.eventParameter = eventParameter;
        this.eventType = eventType;
    }
	public MVCConfigurerEvent(Object eventParameter) {
		this.eventParameter = eventParameter;
    }
	public Class<?> getEventType() {
		return eventType;
	}
	public <T> Class<T> getEventType(Class<T> clazz) {
		if (clazz == eventType) {
			return clazz;
		}
		return null;
	}
	public Object getEventParameter() {
		return eventParameter;
	}
	
	public  <T> T getEventParameter(Class<T> clazz) {
		return ClassAdapter.convert(eventParameter, clazz);
	}

	public Object getEventResult() {
		return eventResult;
	}
	public <T> T getEventResult(Class<T> clazz) {
		return ClassAdapter.convert(eventResult, clazz);
	}

	public void setEventResult(Object eventResult) {
		this.eventResult = eventResult;
	}
}
