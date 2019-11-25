package com.polaris.springmvc;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.pagehelper.util.StringUtil;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.dto.ResultDto;

abstract class RestExceptionHandler {  
    
	@ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception ex){
    	ResultDto responseDto = new ResultDto();
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
        return responseDto.toJSON().toString();  
    }

}  

