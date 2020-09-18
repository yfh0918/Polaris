package com.polaris.ndi.service;

public abstract class LifeCycleServiceFactory {

    public static LifeCycleService get() {
        return VedioGrabberService.INSTANCE;
    }
}
