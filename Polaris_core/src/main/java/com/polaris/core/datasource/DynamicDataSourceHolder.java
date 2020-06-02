package com.polaris.core.datasource;

import com.polaris.core.thread.PolarisInheritableThreadLocal;

public class DynamicDataSourceHolder {

    private static final ThreadLocal<String> holder = new PolarisInheritableThreadLocal<String>();

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
