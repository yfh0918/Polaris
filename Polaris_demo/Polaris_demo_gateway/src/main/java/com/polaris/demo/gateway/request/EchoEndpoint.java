package com.polaris.demo.gateway.request;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.proxy.jersey.JerseyFilter;
import com.polaris.core.Constant;
import com.polaris.core.util.JacksonUtil;
import com.polaris.core.util.ResultUtil;

import io.netty.handler.codec.http.HttpResponseStatus;

@Path("/gateway")
public class EchoEndpoint extends JerseyFilter {

    private String fileContent = null;
    
    public void onChange(HttpFile file) {
        fileContent = file.getData();
    };
    
    @Path("/cc/ip")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response echo(@Context Request request, @Context HttpHeaders headers, @QueryParam("test") String test) {
        return Response.status(Status.FORBIDDEN)
                .entity(test + fileContent)
                .build();
    }
    @Path("/cc/test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response echo2(@Context Request request, @Context HttpHeaders headers) {
        String message = JacksonUtil.toJson(HttpFilterMessage.of(
                ResultUtil.create(Constant.RESULT_FAIL,"fasdfasdf").toJSONString(),
                HttpResponseStatus.FORBIDDEN));
        return Response.status(Status.FORBIDDEN)
                .entity(message)
                .build();
    }
    
    @Path("/cc/test2")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response echo3(@Context Request request, @Context HttpHeaders headers) {
        return Response.status(Status.OK)
                .entity(null)
                .build();
    }
}
