package com.polaris.container.gateway;

import java.util.Comparator;

//排序器
public class HttpFilterCompare implements Comparator<HttpFilter> {
	@Override
    public int compare(HttpFilter o1, HttpFilter o2) {
		return o1.getHttpFilterEntity().getOrder().compareTo(o2.getHttpFilterEntity().getOrder());
    }
}
