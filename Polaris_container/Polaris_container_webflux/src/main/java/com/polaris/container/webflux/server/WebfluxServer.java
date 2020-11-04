package com.polaris.container.webflux.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.polaris.container.ServerOrder;
import com.polaris.container.SpringContextServer;
import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.Constant;
import com.polaris.core.GlobalContext;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringContextHealper;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.UuidUtil;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

@EnableWebFlux
@Order(ServerOrder.WEBFLUX)
public class WebfluxServer extends SpringContextServer{
    
    private static Logger logger = LoggerFactory.getLogger(WebfluxServer.class);
    
    private DisposableServer disposableSever;
    
    private int port = 80;
    
    private String contextPath;
    
    private String contextPathForJudge;
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        ConfigurationHelper.addConfiguration(WebfluxServer.class);
        super.start();
        
        //通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(SpringContextHealper.getApplicationContext()).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        port = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE));
        HttpServer server =
                HttpServer.create()
                          .port(port)
                          .forwarded(Boolean.parseBoolean(ConfClient.get("server.forwarded","true")))
                          .compress(Boolean.parseBoolean(ConfClient.get("server.compress","true")))//压缩
                          .handle(httpHandlerAdapter);
        
        
        //ssl set
        server = secure(server);
        if (server == null) {
            return;
        }
        
        //protocals set
        server.protocol(listProtocols());
        
        //bind server
        disposableSever = server.bindNow();

        //log
        logger.info("netty-webflux started on port(s) " + port + " with context path '"+contextPath+"'");
        
        //block
        new Thread(new Runnable() {
            @Override
            public void run() {
                disposableSever.onDispose().block();
            }
        }, "disposableSever-onDispose-block").start();
        
    }
    
    private void setCcontextPath() {
        contextPath = ConfClient.get(Constant.SERVER_CONTEXT,ConfClient.get(Constant.SERVER_SPRING_CONTEXT, Constant.SLASH));
        contextPathForJudge = contextPath;
        if (!contextPathForJudge.endsWith(Constant.SLASH)) {
            contextPathForJudge = contextPathForJudge + Constant.SLASH;
        }
    }
    
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter traceFilter() {
        setCcontextPath();
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            try {
                //traceId
                String traceId = request.getHeaders().getFirst(GlobalContext.TRACE_ID);
                if (StringUtil.isEmpty(traceId)) {
                    GlobalContext.setTraceId(UuidUtil.generateUuid());
                }  else {
                    GlobalContext.setTraceId(traceId);
                }
                
                //parentId
                String parentId = request.getHeaders().getFirst(GlobalContext.SPAN_ID);
                if (StringUtil.isNotEmpty(parentId)) {
                    GlobalContext.setParentId(parentId);
                } 
                
                //spanId
                GlobalContext.setSpanId(UuidUtil.generateUuid());
                
                //streamId
                String streamId = request.getHeaders().getFirst(GlobalContext.STREAM_ID);
                if (streamId != null) {
                    response.getHeaders().add(GlobalContext.STREAM_ID, streamId);
                }
                
                if (request.getURI().getPath().startsWith(contextPathForJudge)) {
                    return chain.filter(
                        exchange.mutate()
                        .request(request.mutate().contextPath(contextPath).build())
                        .build());
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return response.setComplete();
                }
            } finally {
                GlobalContext.removeContext();
            }
        };
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        disposableSever.disposeNow();
    }
    
    private HttpProtocol[] listProtocols() {
        boolean ssl = Boolean.parseBoolean(ConfClient.get("server.tls.enable","false"));
        boolean http2 = Boolean.parseBoolean(ConfClient.get("server.http2.enable","false"));
        if (http2) {
            if (ssl) {
                return new HttpProtocol[] { HttpProtocol.H2, HttpProtocol.HTTP11 };
            }
            else {
                return new HttpProtocol[] { HttpProtocol.H2C, HttpProtocol.HTTP11 };
            }
        }
        return new HttpProtocol[] { HttpProtocol.HTTP11 };
    }
    
    private HttpServer secure(HttpServer server) throws Exception{
        boolean ssl = Boolean.parseBoolean(ConfClient.get("server.ssl.enable","false"));
        if (!ssl) {
            return server;
        }
        String certificate = ConfClient.get("server.certificate.file");
        boolean isCertificate = false;
        File certificateFile = null;
        if (StringUtil.isNotEmpty(certificate)) {
            certificateFile = new File(certificate);
            if (certificateFile.isFile()) {
                isCertificate = true;
            }
        }
        String privateKey = ConfClient.get("server.privateKey.file");
        boolean isPrivateKey = false;
        File privateKeyFile = null;
        if (StringUtil.isNotEmpty(privateKey)) {
            privateKeyFile = new File(privateKey);
            if (privateKeyFile.isFile()) {
                isPrivateKey = true;
            }
        }
        if (!isCertificate || !isPrivateKey) {
            SelfSignedCertificate cert = new SelfSignedCertificate();
            certificateFile = cert.certificate();
            privateKeyFile = cert.privateKey();
        }
        SslContextBuilder sslContextBuilder =
        SslContextBuilder.forServer(certificateFile, privateKeyFile);
        server.secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder));
        return server;
    }

}
