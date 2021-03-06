package com.polaris.container.servlet.resteasy;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.pojo.Result;
import com.polaris.core.util.StringUtil;

@Provider  
public class ResteasyExceptionHandler implements ExceptionMapper<Exception>{  
    
	private static final Logger logger = LoggerFactory.getLogger(ResteasyExceptionHandler.class);
	
    @SuppressWarnings({ "rawtypes" })
	@Override  
    public Response toResponse(Exception ex) {
    	Result responseDto = new Result();
    	responseDto.setCode(Constant.RESULT_FAIL);
    	if (ex instanceof RuntimeException) {
        	responseDto.setMessage(Constant.MESSAGE_GLOBAL_ERROR);
    	} else {
    		
        	String errorCode = ex.getMessage();
        	if (StringUtil.isNotEmpty(errorCode)) {
        		if (errorCode.contains(":")) {
        			errorCode = errorCode.substring(errorCode.indexOf(":") + 1);
        		} 
        		if(errorCode.contains("\r\n")) {
        			errorCode = errorCode.substring(0,errorCode.indexOf("\r\n"));
        		}
        		if (errorCode.contains("\n")) {
        			errorCode = errorCode.substring(0,errorCode.indexOf("\n"));
        		}
        		errorCode = errorCode.trim();
            	responseDto.setMessage(ConfClient.get(errorCode,errorCode));
        	}
    	}
    	Response response = Response.status(200).entity(responseDto.toString()).build();
    	response.getHeaders().remove("Content-Type");
    	response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON);
    	logger.error(ex.toString());
        return response;  
    }
}  

