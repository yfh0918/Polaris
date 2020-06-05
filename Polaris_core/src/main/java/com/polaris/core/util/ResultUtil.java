package com.polaris.core.util;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.polaris.core.Constant;
import com.polaris.core.pojo.Result;

/**
 * @author 
 * @ClassName: ResultUtil
 * @Description: 接口返回的结果工具类
 * @date 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ResultUtil {

	private static final String MSG_SUCCESS = "success";

    private static final String MSG_FAIL = "fail";

    public static Result success() {
        return ResultUtil.success(MSG_SUCCESS);
    }


    public static Result success(Object val) {
        return ResultUtil.success(MSG_SUCCESS, val);
    }

    public static Result success(String msg, Object val) {
        return new Result(Constant.RESULT_SUCCESS, msg, val);
    }

    public static Result fail() {
        return ResultUtil.fail(MSG_FAIL);
    }

    public static Result fail(String msg) {
        return new Result(Constant.RESULT_FAIL, msg);
    }

    public static Result fail(String msg, String detailMsg, Object val) {
        return new Result(Constant.RESULT_FAIL, msg, detailMsg, val);
    }

	public static Result fail(String msg, Object val) {
        return new Result(Constant.RESULT_FAIL, msg, val);
    }
	
	public static Result get(String json) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		try {
			return JacksonUtil.toObj(json, new com.fasterxml.jackson.core.type.TypeReference<Result>() {});
    	} catch (Exception ex) {
    		return null;
    	}
	}
	
	public static Result create(String code,  String message) {
    	Result result = new Result();
    	result.setCode(code);
    	result.setMessage(message);
    	return result;
    }
	
	public static <T> List<T> toList(Result result, Class<T> clazz) {
		return toList(result, clazz, Constant.RESULT_SUCCESS);
    }
	public static <T> List<T> toList(Result result, Class<T> clazz, String successCode) {
		if (result != null && result.getCode().equals(successCode) && result.getData() != null) {
		    List<T> ts = JacksonUtil.toObj(JacksonUtil.toJson(result.getData()), new TypeReference<List<T>>(){});
	        return ts;
		}
		return null;
    }
	public static <T> T toObject(Result result, Class<T> clazz) {
		return toObject(result, clazz, Constant.RESULT_SUCCESS);
	}
	public static <T> T toObject(Result result, Class<T> clazz, String successCode) {
		if (result != null && result.getCode().equals(successCode) && result.getData() != null) {
		    return JacksonUtil.toObj(JacksonUtil.toJson(result.getData()), clazz);
		}
		return null;
    }

}
