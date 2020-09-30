package com.polaris.container.gateway;

import com.polaris.container.gateway.pojo.HttpFilterEntityEnum;
import com.polaris.core.component.Initial;

public class HttpFilterInit implements Initial {
	@Override
	public void init() {
		for (HttpFilterEntityEnum e : HttpFilterEntityEnum.values()) {
			HttpFilterHelper.addFilter(e.getFilterEntity());
		}
	}
}
