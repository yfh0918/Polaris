package com.polaris.container.gateway.proxy.jersey;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.polaris.core.config.ConfClient;

public class JerseyConfig extends ResourceConfig {
    public static Set<JerseyFilter> END_POINTS = new HashSet<>();
    private volatile AtomicBoolean initialized = new AtomicBoolean(false);
    public JerseyConfig() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        setApplicationName(ConfClient.getAppName());
        register(JacksonFeature.class);
        register(LoggingFeature.class);
        register(MultiPartFeature.class);
        for (JerseyFilter filter : END_POINTS) {
            this.register(filter);
        }
    }
    public static void add(JerseyFilter filter) {
        END_POINTS.add(filter);
    }
    
    public static boolean hasEndPoints() {
        return END_POINTS.size() == 0 ? false : true;
    }
}
