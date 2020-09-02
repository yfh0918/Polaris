/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.polaris.container.gateway.proxy.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeEvent;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

/**
 * Negotiates with the browser if HTTP2 or HTTP is going to be used. Once decided, the Netty
 * pipeline is setup with the correct handlers for the selected protocol.
 */
public class Http2OrHttpHandler extends ApplicationProtocolNegotiationHandler {

    private Http11Listener listener;
    public Http2OrHttpHandler(Http11Listener listener) {
        super(ApplicationProtocolNames.HTTP_1_1);
        this.listener = listener;
    }
    
    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        
        // for h2
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            ctx.pipeline().addLast(createHttpToHttp2ConnectionHandler());
            ctx.pipeline().addLast(new Http2SettingsHandler());
            ctx.pipeline().addLast(new Http2EmptyHandler());
            listener.onHttp11(ctx.pipeline(),true);
            return;
        }

        // for http1.1
        if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            listener.onHttp11(ctx.pipeline(), false);
            return;
        }

        throw new IllegalStateException("unknown protocol: " + protocol);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        
        // for h2
        if (evt instanceof SslHandshakeCompletionEvent) {
            super.userEventTriggered(ctx, evt);
            
        
        // for h2c
        } else if (evt instanceof UpgradeEvent) {
            try {
                ctx.pipeline().addAfter(Http2SettingsHandler.NAME, null, new Http2EmptyHandler());
            } catch (Throwable cause) {
                exceptionCaught(ctx, cause);
            } finally {
                ctx.pipeline().remove(this);
            }
            ctx.fireUserEventTriggered(evt);
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
    
    public static HttpToHttp2ConnectionHandler createHttpToHttp2ConnectionHandler() {
        DefaultHttp2Connection connection = new DefaultHttp2Connection(true);
        HttpToHttp2ConnectionHandler connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(new InboundHttp2ToHttpAdapterBuilder(connection)
                        .maxContentLength(Integer.MAX_VALUE)
                        .propagateSettings(true)
                        .build())
                .connection(connection)
                .build();
        return connectionHandler;
    }
}
