package com.polaris.comm.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ResultDto extends MessageDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public  ResultDto(){
    	super();
    }
    
    public  ResultDto(Integer status,String msgContent){
    	super(status, msgContent);
    }
    
	public JSONObject toJSON() {
		return (JSONObject) JSONObject.toJSON(this);
	}
	
	public JSONObject toJSON(ResultDto dto) {
		return (JSONObject) JSONObject.toJSON(dto);
	}
	
	/**
	 * 属于组件:  作用：自定义单个data 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Map<String, Object> data;
	
	/**
	 * 属于组件: table,tree 作用：tree与table的显示行数据,内在数据字段根据显示自定义 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private List<Map<String, Object>> datas;
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public List<Map<String, Object>> getDatas() {
		return datas;
	}
	
	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
	}

	/**
	 * 请求参数Map
	 */
	private Map<String, Object> parameterMap = new LinkedHashMap<>();
	/**
	 * 获取map
	 */
	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}
	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
}


