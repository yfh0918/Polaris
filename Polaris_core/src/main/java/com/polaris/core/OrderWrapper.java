package com.polaris.core;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class OrderWrapper<T> {
	private final int order;
    private final T handler;

    public OrderWrapper(int order, T handler) {
        this.order = order;
        this.handler = handler;
    }

    public int getOrder() {
        return order;
    }

    public T getHandler() {
        return handler;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void insertSorted(List<OrderWrapper> list, Object object) {
        int order = resolveOrder(object);
        int idx = 0;
        for (; idx < list.size(); idx++) {
            if (list.get(idx).getOrder() > order) {
                break;
            }
        }
        list.add(idx, new OrderWrapper(order, object));
    }

	public static int resolveOrder(Object object) {
        if (!object.getClass().isAnnotationPresent(Order.class)) {
            return Ordered.LOWEST_PRECEDENCE;
        } else {
            return object.getClass().getAnnotation(Order.class).value();
        }
    }
}
