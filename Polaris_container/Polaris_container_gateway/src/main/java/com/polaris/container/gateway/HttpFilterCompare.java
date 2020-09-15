package com.polaris.container.gateway;

import java.util.Comparator;

import io.netty.handler.codec.http.HttpMessage;

//排序器
public class HttpFilterCompare implements Comparator<HttpFilter<? extends HttpMessage>> {
	@Override
    public int compare(HttpFilter<? extends HttpMessage> o1, HttpFilter<? extends HttpMessage> o2) {
		return o1.getHttpFilterEntity().getOrder().compareTo(o2.getHttpFilterEntity().getOrder());
    }
}
