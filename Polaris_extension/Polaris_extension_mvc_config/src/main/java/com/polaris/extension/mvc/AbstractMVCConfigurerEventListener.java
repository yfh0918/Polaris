package com.polaris.extension.mvc;

import java.util.ArrayList;
import java.util.List;

import com.polaris.core.event.EventDispatcher.AbstractEventListener;
import com.polaris.core.event.EventDispatcher.Event;

public abstract class AbstractMVCConfigurerEventListener extends AbstractEventListener{
	@Override
	public List<Class<? extends Event>> interest() {
		List<Class<? extends Event>> types = new ArrayList<Class<? extends Event>>();
        types.add(MVCConfigurerEvent.class);//目前只注册1个感兴趣的事件
        return types;
	}
	
	@Override
	public void onEvent(Event event) {
		if (event instanceof MVCConfigurerEvent) {
			onMVCEvent((MVCConfigurerEvent)event);
		}
	}
	
	protected abstract void onMVCEvent(MVCConfigurerEvent event);
}
