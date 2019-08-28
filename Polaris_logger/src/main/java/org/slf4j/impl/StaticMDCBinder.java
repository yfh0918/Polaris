package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

import com.polaris.core.log.XfliMDCAdapter;

public class StaticMDCBinder {

    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    public MDCAdapter getMDCA() {
        return new XfliMDCAdapter();
    }

    public String getMDCAdapterClassStr() {
        return XfliMDCAdapter.class.getName();
    }
}
