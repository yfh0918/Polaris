package com.polaris.core.dto;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.polaris.core.Constant;

public class ResultDto<T> extends MessageDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private T data;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detailMessage;

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public ResultDto() {
        super(Constant.RESULT_SUCCESS,"");
    }

    public ResultDto(T value) {
    	super(Constant.RESULT_SUCCESS,"");
        this.data = value;
    }

    public ResultDto(String code, String message) {
        super(code,message);
        this.data = null;
    }

    public ResultDto(String code, String message, String detailMessage, T val) {
    	super(code,message);
        this.detailMessage = detailMessage;
        this.data = val;
    }

    public ResultDto(String code, String message, T value) {
    	super(code,message);
        this.data = value;
    }

    public Object getData() {
        return data;
    }

    public void setData(T value) {
        this.data = value;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this
                , SerializerFeature.DisableCircularReferenceDetect
                , SerializerFeature.WriteMapNullValue);
    }

    public Boolean isSuccess() {
        return Objects.equals(Constant.RESULT_SUCCESS, this.getCode());
    }

    /**
     * @return
     */
    public String toJSONString() {
        return JSONObject.toJSONString(this
                , SerializerFeature.DisableCircularReferenceDetect
                , SerializerFeature.WriteMapNullValue);
    }

}


