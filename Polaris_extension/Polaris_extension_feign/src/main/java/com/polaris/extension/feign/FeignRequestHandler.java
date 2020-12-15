package com.polaris.extension.feign;

import com.polaris.core.naming.request.NamingRequestHandler;

public class FeignRequestHandler implements NamingRequestHandler{

    @Override
    public <T> T invoke(Class<T> apiType) {
        return FeignClient.target(apiType);
    }

}
