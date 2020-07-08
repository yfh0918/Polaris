package com.polaris.core.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfHandlerSystem;

/**
 * appName util
 *
 */
public class AppNameUtil {


    private static String appName = null;
    
    public static String getAppName() {
        if (appName != null) {
            return appName;
        }
        appName = getAppNameByProjectName();
        if (appName != null) {
            return appName;
        }
        appName = getAppNameBySpringBoot();
        if (appName != null) {
            return appName;
        }
        appName = getAppNameByJavaCommond();
        if (appName != null) {
            return appName;
        }
        return "unknown";
    }

    private static String getAppNameByProjectName() {
        return ConfHandlerSystem.getProperties().getProperty(Constant.PARAM_MARKING_PROJECT);
    }
    
    private static String getAppNameBySpringBoot() {
        return ConfHandlerSystem.getProperties().getProperty(Constant.PARAM_MARKING_SPRINGBOOT);
    }

    private static String getAppNameByJavaCommond() {
        
        // parse sun.java.command property
        String command = ConfHandlerSystem.getProperties().getProperty(Constant.SUN_JAVA_COMMAND);
        if (StringUtils.isEmpty(command)) {
            return null;
        }
        command = command.split("\\s")[0];
        String separator = File.separator;
        if (command.contains(separator)) {
            String[] strs;
            if ("\\".equals(separator)) {
                strs = command.split("\\\\");
            } else {
                strs = command.split(separator);
            }
            command = strs[strs.length - 1];
        }
        if (command.endsWith(Constant.JAR_SUFFIX_LOWER) || command.endsWith(Constant.JAR_SUFFIX_UPPER)) {
            command = command.substring(0, command.length() - 4);
        }
        return command;
    }
}
