package com.polaris.gateway.support;

import com.polaris.core.Constant;
import com.polaris.core.dto.ResultDto;

public class HttpRequestFilterSupport {

    //创建ResponseDto
    public static ResultDto createResultDto(String message) {
    	return createResultDto(Constant.STATUS_FAILED, message);
    }
    public static ResultDto createResultDto(Exception ex) {
    	return createResultDto(Constant.STATUS_FAILED, ex.getMessage());
    }
    public static ResultDto createResultDto(int status,  String message) {
    	ResultDto resultDto = new ResultDto();
    	resultDto.setStatus(String.valueOf(status));
    	resultDto.setMsgContent(message);
    	return resultDto;
    }
}
