package com.polaris.extension.db.datasource;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.logging.Logger;

import javax.sql.DataSource;

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
    
    @Override
    public DataSource determineTargetDataSource() {
    	return super.determineTargetDataSource();
    }

}
