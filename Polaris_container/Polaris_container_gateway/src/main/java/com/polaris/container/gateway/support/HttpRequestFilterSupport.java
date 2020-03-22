package com.polaris.container.gateway.support;

import com.polaris.core.Constant;
import com.polaris.core.pojo.Result;

@SuppressWarnings("rawtypes")
public class HttpRequestFilterSupport {

    //创建ResponseDto
    public static Result createResultDto(String message) {
    	return createResultDto(Constant.RESULT_FAIL, message);
    }
    public static Result createResultDto(Exception ex) {
    	return createResultDto(Constant.RESULT_FAIL, ex.getMessage());
    }
	public static Result createResultDto(String code,  String message) {
    	Result resultDto = new Result();
    	resultDto.setCode(code);
    	resultDto.setMessage(message);
    	return resultDto;
    }
}
