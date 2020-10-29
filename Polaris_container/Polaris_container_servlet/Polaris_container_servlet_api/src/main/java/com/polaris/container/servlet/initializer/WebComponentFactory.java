package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.core.component.Initial;

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
    
    public static Initial getOrCreate(ConfigurableApplicationContext springContext, ServletContext servletContext, WebComponent compoent) {
        if (compoent == WebComponent.INIT) {
            if (INIT == null) {
                INIT = new WebInitParamRegister(springContext,servletContext);
            }
            return INIT;
        } else if (compoent == WebComponent.LISTENER) {
            if (LISTENER == null) {
                LISTENER = new WebListenerRegister(springContext,servletContext);
            }
            return LISTENER;
        } else if (compoent == WebComponent.FILTER) {
            if (FILTER == null) {
                FILTER = new WebFilterRegister(springContext,servletContext);
            }
            return FILTER;
        } else if (compoent == WebComponent.SERVLET) {
            if (SERVLET == null) {
                SERVLET = new WebServletRegister(springContext,servletContext);
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
    public static void init(ConfigurableApplicationContext springContext, ServletContext servletContext,WebComponent... components) {
        if (components == null) {
            return;
        }
        for (WebComponent component : components) {
            init(getOrCreate(springContext,servletContext,component));
        }
    }
}
