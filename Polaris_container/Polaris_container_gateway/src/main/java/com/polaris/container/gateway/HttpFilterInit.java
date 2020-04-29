package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterEntityEnum;
import com.polaris.core.config.ConfEndPoint;

public class HttpFilterInit implements ConfEndPoint {
	@Override
	public void init() {
		for (HttpFilterEntityEnum e : HttpFilterEntityEnum.values()) {
			HttpFilterHelper.INSTANCE.addFilter(e.getFilterEntity());
		}
	}
}
