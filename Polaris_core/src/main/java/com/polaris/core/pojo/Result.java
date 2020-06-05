package com.polaris.core.pojo;

import java.io.Serializable;
import java.util.Objects;

import com.polaris.core.Constant;
import com.polaris.core.util.JacksonUtil;

public class Result<T> extends Message implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private T data;
    
    private String detailMessage;

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public Result() {
        super(Constant.RESULT_SUCCESS,"");
    }

    public Result(T value) {
    	super(Constant.RESULT_SUCCESS,"");
        this.data = value;
    }

    public Result(String code, String message) {
        super(code,message);
        this.data = null;
    }

    public Result(String code, String message, String detailMessage, T val) {
    	super(code,message);
        this.detailMessage = detailMessage;
        this.data = val;
    }

    public Result(String code, String message, T value) {
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
        return JacksonUtil.toJson(this);
    }

    public Boolean isSuccess() {
        return Objects.equals(Constant.RESULT_SUCCESS, this.getCode());
    }

    /**
     * @return
     */
    public String toJSONString() {
        return toString();
    }

}


