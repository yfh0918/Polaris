package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolConnection {
    private static boolean INITIAL = true;
    private static int ACCEPTOR_THREADS = 1;
    
    private static int CLIENT_TO_PROXY_WORKER_THREADS = 40;
    
    private static int PROXY_TO_SERVER_WORKER_THREADS = 40;
    
    private static int IDLE_TIMEOUT = 60;
    
    private static int TIMEOUT = 40000;
    
    public static int getAcceptorThreads() {
        init();
        return ACCEPTOR_THREADS;
    }
    public static int getClientToProxyWorkerThreads() {
        init();
        return CLIENT_TO_PROXY_WORKER_THREADS;
    }
    public static int getProxyToServerWorkerThreads() {
        init();
        return PROXY_TO_SERVER_WORKER_THREADS;
    }
    public static int getIdleTimeout() {
        init();
        return IDLE_TIMEOUT;
    }
    public static int getTimeout() {
        init();
        return TIMEOUT;
    }
    
    private static void init() {
        if (INITIAL) {
            String acceptorThreads = HttpProtocol.getConnectionMap().get("acceptorThreads");
            if (StringUtil.isNotEmpty(acceptorThreads)) {
                try {
                    ACCEPTOR_THREADS = Integer.parseInt(acceptorThreads);
                } catch (Exception ex) {
                }
            }
            String clientToProxyWorkerThreads = HttpProtocol.getConnectionMap().get("clientToProxyWorkerThreads");
            if (StringUtil.isNotEmpty(clientToProxyWorkerThreads)) {
                try {
                    CLIENT_TO_PROXY_WORKER_THREADS = Integer.parseInt(clientToProxyWorkerThreads);
                } catch (Exception ex) {
                }
            }
            String proxyToServerWorkerThreads = HttpProtocol.getConnectionMap().get("proxyToServerWorkerThreads");
            if (StringUtil.isNotEmpty(proxyToServerWorkerThreads)) {
                try {
                    PROXY_TO_SERVER_WORKER_THREADS = Integer.parseInt(proxyToServerWorkerThreads);
                } catch (Exception ex) {
                }
            }
            String idleTimeout = HttpProtocol.getConnectionMap().get("idleTimeout");
            if (StringUtil.isNotEmpty(idleTimeout)) {
                try {
                    IDLE_TIMEOUT = Integer.parseInt(idleTimeout);
                } catch (Exception ex) {
                }
            }
            String timeout = HttpProtocol.getConnectionMap().get("timeout");
            if (StringUtil.isNotEmpty(timeout)) {
                try {
                    TIMEOUT = Integer.parseInt(timeout);
                } catch (Exception ex) {
                }
            }
            
          INITIAL = false;
        }
    }
}
