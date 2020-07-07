package com.polaris.container.servlet.initializer;

import com.polaris.core.component.Initial;
import com.polaris.core.util.SpringUtil;

public abstract class WebComponentFactory {
    
    public enum WebComponent {
        INIT,//init param
        LISTENER,//listener
        FILTER,//filter
        SERVLET;//servlet
    }
    
    private static Initial INIT = null;
    private static Initial LISTENER = null;
    private static Initial FILTER = null;
    private static Initial SERVLET = null;
    
    public static Initial getOrCreate(WebComponent compoent) {
        if (compoent == WebComponent.INIT) {
            if (INIT == null) {
                INIT = new WebInitParamRegister(SpringUtil.getApplicationContext(),ServletContextHelper.getServletContext());
            }
            return INIT;
        } else if (compoent == WebComponent.LISTENER) {
            if (LISTENER == null) {
                LISTENER = new WebListenerRegister(SpringUtil.getApplicationContext(),ServletContextHelper.getServletContext());
            }
            return LISTENER;
        } else if (compoent == WebComponent.FILTER) {
            if (FILTER == null) {
                FILTER = new WebFilterRegister(SpringUtil.getApplicationContext(),ServletContextHelper.getServletContext());
            }
            return FILTER;
        } else if (compoent == WebComponent.SERVLET) {
            if (SERVLET == null) {
                SERVLET = new WebServletRegister(SpringUtil.getApplicationContext(),ServletContextHelper.getServletContext());
            }
            return SERVLET;
        } 
        return null;
    }
    
    public static void init(Initial...initials) {
        if (initials == null) {
            return;
        }
        for (Initial initial : initials) {
            initial.init();
        }
    }
    public static void init(WebComponent... components) {
        if (components == null) {
            return;
        }
        for (WebComponent component : components) {
            init(getOrCreate(component));
        }
    }
}
