package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolConnection {
    private static int ACCEPTOR_THREADS = 1;
    
    private static int CLIENT_TO_PROXY_WORKER_THREADS = 40;
    
    private static int PROXY_TO_SERVER_WORKER_THREADS = 40;
    
    private static int IDLE_TIMEOUT = 60;
    
    private static int TIMEOUT = 40000;
    
    private static long READ_THROTTLE_BYTES_PER_SECOND = 0l;
    
    private static long WRITE_THROTTLE_BYTES_PER_SECOND = 0l;
    static {
        init();
    }
    public static int getAcceptorThreads() {
        return ACCEPTOR_THREADS;
    }
    public static int getClientToProxyWorkerThreads() {
        return CLIENT_TO_PROXY_WORKER_THREADS;
    }
    public static int getProxyToServerWorkerThreads() {
        return PROXY_TO_SERVER_WORKER_THREADS;
    }
    public static int getIdleTimeout() {
        return IDLE_TIMEOUT;
    }
    public static int getTimeout() {
        return TIMEOUT;
    }
    
    private static void init() {
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
        
        String readThrottleBytesPerSecond = HttpProtocol.getConnectionMap().get("readThrottleBytesPerSecond");
        if (StringUtil.isNotEmpty(readThrottleBytesPerSecond)) {
            try {
                READ_THROTTLE_BYTES_PER_SECOND = Long.parseLong(readThrottleBytesPerSecond);
            } catch (Exception ex) {
            }
        }
        
        String writeThrottleBytesPerSecond = HttpProtocol.getConnectionMap().get("writeThrottleBytesPerSecond");
        if (StringUtil.isNotEmpty(writeThrottleBytesPerSecond)) {
            try {
                WRITE_THROTTLE_BYTES_PER_SECOND = Long.parseLong(writeThrottleBytesPerSecond);
            } catch (Exception ex) {
            }
        }
    }
    public static long getReadThrottleBytesPerSecond() {
        return READ_THROTTLE_BYTES_PER_SECOND;
    }
    public static void setReadThrottleBytesPerSecond(long readThrottleBytesPerSecond) {
        HttpProtocolConnection.READ_THROTTLE_BYTES_PER_SECOND = readThrottleBytesPerSecond;
    }
    public static long getWriteThrottleBytesPerSecond() {
        return WRITE_THROTTLE_BYTES_PER_SECOND;
    }
    public static void setWriteThrottleBytesPerSecond(long writeThrottleBytesPerSecond) {
        HttpProtocolConnection.WRITE_THROTTLE_BYTES_PER_SECOND = writeThrottleBytesPerSecond;
    }
}
