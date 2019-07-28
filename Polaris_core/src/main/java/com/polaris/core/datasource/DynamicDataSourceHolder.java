package com.polaris.core.datasource;

import com.polaris.core.thread.InheritablePolarisThreadLocal;

public class DynamicDataSourceHolder {

    private static final ThreadLocal<String> holder = new InheritablePolarisThreadLocal<String>();

    public static String getDataSource() {
        return holder.get();
    }

    public static void setDataSource(String dataSourceName) {
        holder.set(dataSourceName);
    }
    
    public static void remove() {
    	holder.remove();
    }
}
