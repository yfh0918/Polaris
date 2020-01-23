package com.polaris.container.gateway;

public abstract class HttpFilterOrder {
    public Integer getOrder() {
        return HttpFilterEnum.getOrder(this.getClass());
    }
}
