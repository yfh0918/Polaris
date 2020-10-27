package com.polaris.container.gateway.proxy.jersey;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.core.Feature;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.polaris.core.config.ConfClient;

public class JerseyConfig extends ResourceConfig {
    public static Set<Class<? extends Feature>> FEATURES = new HashSet<>();
    public static Set<JerseyFilter> END_POINTS = new HashSet<>();
    private volatile AtomicBoolean initialized = new AtomicBoolean(false);
    static {
        FEATURES.add(JacksonFeature.class);
        FEATURES.add(LoggingFeature.class);
        FEATURES.add(MultiPartFeature.class);
    }
    public JerseyConfig() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        setApplicationName(ConfClient.getAppName());
        for (Class<? extends Feature> feature : FEATURES) {
            this.register(feature);
        }
        for (JerseyFilter filter : END_POINTS) {
            this.register(filter);
        }
    }
    public static void addFilter(JerseyFilter filter) {
        END_POINTS.add(filter);
    }
    
    public static boolean hasEndPoints() {
        return END_POINTS.size() == 0 ? false : true;
    }
    
    public static void addFeature(Class<? extends Feature> clazz) {
        FEATURES.add(clazz);
    }
}
