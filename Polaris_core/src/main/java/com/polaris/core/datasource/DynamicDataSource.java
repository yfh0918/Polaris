package com.polaris.core.datasource;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.logging.Logger;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSource();
    }

    @Override
    public Logger getParentLogger() {
        // TODO Auto-generated method stub
        return null;
    }

}
