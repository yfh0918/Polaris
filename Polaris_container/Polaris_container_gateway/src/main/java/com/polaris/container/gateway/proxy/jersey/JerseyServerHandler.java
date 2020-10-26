/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.polaris.container.gateway.proxy.jersey;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.internal.ContainerUtils;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;

import com.polaris.container.gateway.pojo.HttpProtocolTls;
import com.polaris.container.gateway.util.ResponseUtil;
import com.polaris.core.util.JacksonUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * {@link io.netty.channel.ChannelInboundHandler} which servers as a bridge
 * between Netty and Jersey.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class JerseyServerHandler {

    private volatile ApplicationHandler appHandler;
    
    public static JerseyServerHandler INSTANCE = new JerseyServerHandler();
    
    /**
     * Constructor.
     *
     * @param baseUri   base {@link URI} of the container (includes context path, if any).
     * @param container Netty container implementation.
     */
    private JerseyServerHandler() {
        this.appHandler = new ApplicationHandler(new JerseyConfig());
    }

    
    public HttpResponse clientToProxyRequest(final ChannelHandlerContext ctx, HttpRequest req) {
        ContainerRequest requestContext;
        try {
            requestContext = createContainerRequest(ctx, req);
            requestContext.setWriter(new ContainerResponseWriter() {
                @Override
                public OutputStream writeResponseStatusAndHeaders(long contentLength,
                        ContainerResponse responseContext) throws ContainerException {
                    return null;
                }

                @Override
                public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
                    return false;
                }

                @Override
                public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
                }

                @Override
                public void commit() {
                }

                @Override
                public void failure(Throwable error) {
                }

                @Override
                public boolean enableResponseBuffering() {
                    return false;
                }
                
            });
            final ContainerResponse containerResponse = appHandler
                    .apply(requestContext)
                    .get();
            Object entity = containerResponse.getEntity();
            if (entity != null) {
                FullHttpResponse response = ResponseUtil.createResponse(
                        req,JacksonUtil.toJson(entity),
                        HttpResponseStatus.valueOf(containerResponse.getStatus()));
                return response;
            }
        } catch (Exception e) {
            throw new JerseyServerException();
        }
    
        return null;
    }

    /**
     * Create Jersey {@link ContainerRequest} based on Netty {@link HttpRequest}.
     *
     * @param ctx Netty channel context.
     * @param req Netty Http request.
     * @return created Jersey Container Request.
     */
    private ContainerRequest createContainerRequest(ChannelHandlerContext ctx, HttpRequest req) throws URISyntaxException{
        HttpHeaders headers = req.headers();
        URI baseUri = new URI((HttpProtocolTls.isTlsEnable() ? "https" : "http") + "://" + headers.get(HttpHeaderNames.HOST) + "/");
        String s = req.uri().startsWith("/") ? req.uri().substring(1) : req.uri();
        URI requestUri = URI.create(baseUri + ContainerUtils.encodeUnsafeCharacters(s));

        ContainerRequest requestContext = new ContainerRequest(
                baseUri, requestUri, req.method().name(), getSecurityContext(),
                new PropertiesDelegate() {

                    private final Map<String, Object> properties = new HashMap<>();

                    @Override
                    public Object getProperty(String name) {
                        return properties.get(name);
                    }

                    @Override
                    public Collection<String> getPropertyNames() {
                        return properties.keySet();
                    }

                    @Override
                    public void setProperty(String name, Object object) {
                        properties.put(name, object);
                    }

                    @Override
                    public void removeProperty(String name) {
                        properties.remove(name);
                    }
                },null);

        
        // copying headers from netty request to jersey container request context.
        for (String name : req.headers().names()) {
            requestContext.headers(name, req.headers().getAll(name));
        }

        return requestContext;
    }

    private SecurityContext getSecurityContext() {
        return new SecurityContext() {

            @Override
            public boolean isUserInRole(final String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        };
    }

}
