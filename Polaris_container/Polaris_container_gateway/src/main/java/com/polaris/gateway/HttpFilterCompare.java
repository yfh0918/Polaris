package com.polaris.gateway;

import java.util.Comparator;

//排序器
public class HttpFilterCompare implements Comparator<HttpFilterOrder> {
	@Override
    public int compare(HttpFilterOrder o1, HttpFilterOrder o2) {
		return o1.getOrder().compareTo(o2.getOrder());
    }
}
