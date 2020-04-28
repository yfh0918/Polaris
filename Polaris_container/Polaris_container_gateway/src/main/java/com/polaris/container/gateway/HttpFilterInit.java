package com.polaris.container.gateway;

import com.polaris.core.config.ConfEndPoint;

public class HttpFilterInit implements ConfEndPoint {
	@Override
	public void init() {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			HttpFilterHelper.INSTANCE.addFilter(e.getFilterEntity());
		}
	}
}
