package com.polaris.gateway.request;

import java.io.File;
import java.io.IOException;

import com.polaris.gateway.GatewayConstant;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public enum FilterType {
	
    ARGS(GatewayConstant.config + File.separator +"args.txt"),
    COOKIE(GatewayConstant.config + File.separator +"cookie.txt"),
    UA(GatewayConstant.config + File.separator +"ua.txt"),
    URL(GatewayConstant.config + File.separator +"url.txt"),
    WURL(GatewayConstant.config + File.separator +"wurl.txt"),
    POST(GatewayConstant.config + File.separator +"post.txt"),
    IP(GatewayConstant.config + File.separator +"ip.txt"),
    WIP(GatewayConstant.config + File.separator +"wip.txt"),
    FILE(GatewayConstant.config + File.separator +"file.txt");

	private static LogUtil logger = LogUtil.getInstance(FilterType.class);
    private String fileName;
    private long lastModified;
	
    FilterType(String fileName) {
        this.fileName = fileName;
    	try {
			lastModified = new File(PropertyUtils.getFilePath(fileName)).lastModified();
		} catch (IOException e) {
			lastModified = -1l;
		}
    }

    public String getFileName() {
        return fileName;
    }
    public boolean isModified() {
    	long templastModified = -1l;
        try {
        	templastModified = new File(PropertyUtils.getFilePath(fileName)).lastModified();
		} catch (IOException e) {
			logger.error(e);
		}
        if (lastModified == templastModified) {
        	return false;
        }
        lastModified = templastModified;
        return true;
    }
    public boolean isNotModified() {
    	return !isModified();
    }
}
