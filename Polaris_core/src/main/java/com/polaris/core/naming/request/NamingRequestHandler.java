package com.polaris.core.naming.request;

public interface NamingRequestHandler {
    <T> T invoke(Class<T> apiType);
}