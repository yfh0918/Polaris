package com.polaris.core.util;

import com.polaris.core.Constant;
import com.polaris.core.dto.ResultDto;

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

    public static ResultDto success() {
        return ResultUtil.success(MSG_SUCCESS);
    }


    public static ResultDto success(Object val) {
        return ResultUtil.success(MSG_SUCCESS, val);
    }

    public static ResultDto success(String msg, Object val) {
        return new ResultDto(Constant.RESULT_SUCCESS, msg, val);
    }

    public static ResultDto fail() {
        return ResultUtil.fail(MSG_FAIL);
    }

    public static ResultDto fail(String msg) {
        return new ResultDto(Constant.RESULT_FAIL, msg);
    }

    public static ResultDto fail(String msg, String detailMsg, Object val) {
        return new ResultDto(Constant.RESULT_FAIL, msg, detailMsg, val);
    }

	public static ResultDto fail(String msg, Object val) {
        return new ResultDto(Constant.RESULT_FAIL, msg, val);
    }

}
