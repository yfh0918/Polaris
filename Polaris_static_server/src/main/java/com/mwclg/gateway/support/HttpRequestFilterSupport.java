package com.mwclg.gateway.support;

import com.mwclg.common.entity.Result;

public class HttpRequestFilterSupport {

    //创建Response
    public static Result createResult(String message) {
    	return createResult("9999", message);
    }
    public static Result createResult(Exception ex) {
    	return createResult("9999", ex.getMessage());
    }
    public static Result createResult(String code,  String message) {
    	return new Result(code,message);
    }
}
