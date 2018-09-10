package com.polaris.comm.dto;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.polaris.comm.dto.BaseDto;

public class ResultDto extends BaseDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    public final static String MSGTYPE_SUCCESS = "success";
    
    public final static String MSGTYPE_DANGER = "danger";
    
    public final static String MSGTYPE_INFO="info";
    
    public final static String MSGTYPE_WARINNING="warning";
    
    public  ResultDto(){
    	
    }
    
    public  ResultDto(Integer status,String msgContent){
    	super(status, msgContent);
    }
    
	public JSONObject toJSON() {
		return (JSONObject) JSONObject.toJSON(this);
	}
	
	public JSONObject toJSON(BaseDto dto) {
		return (JSONObject) JSONObject.toJSON(dto);
	}
	
}


