package com.polaris.gateway.support;

import com.polaris.core.Constant;
import com.polaris.core.dto.ResultDto;

public class HttpRequestFilterSupport {

    //创建ResponseDto
    public static ResultDto createResultDto(String message) {
    	return createResultDto(Constant.RESULT_FAIL, message);
    }
    public static ResultDto createResultDto(Exception ex) {
    	return createResultDto(Constant.RESULT_FAIL, ex.getMessage());
    }
    public static ResultDto createResultDto(String code,  String message) {
    	ResultDto resultDto = new ResultDto();
    	resultDto.setCode(code);
    	resultDto.setMessage(message);
    	return resultDto;
    }
}
