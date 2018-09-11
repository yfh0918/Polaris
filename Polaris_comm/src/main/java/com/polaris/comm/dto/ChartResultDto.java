package com.polaris.comm.dto;


import com.alibaba.fastjson.JSONObject;
import com.polaris.comm.dto.BaseDto;


/**
 * 
 *
 *ChartResponseDto.java 文件使用说明
 * 说明：图表类返回数据结构
 *
 * @version ver 4.0.0
 * @author Shanghai Kinstar Tom.Yu Software .co.ltd. XuChuanHou
 * @since 作成日期：2017年8月22日（XuChuanHou）<br/>
 *        改修日期：
 */
public class ChartResultDto extends ResultDto {
	
	private static final long serialVersionUID = 1L;
	
	 public  ChartResultDto(Integer status,String msgContent){
	    	super(status, msgContent);
	    	setStatus(status);
	    }
	
	private Object xAxis;
	private Object yAxis;
	
	private Object ydata;
	
	/**
	 * @return the xAxis
	 */
	public Object getxAxis() {
		return xAxis;
	}
	
	/**
	 * @param xAxis the xAxis to set
	 */
	public void setxAxis(Object xAxis) {
		this.xAxis = xAxis;
	}
	/**
	 * @return the ydata
	 */
	public Object getYdata() {
		return ydata;
	}
	/**
	 * @param ydata the ydata to set
	 */
	public void setYdata(Object ydata) {
		this.ydata = ydata;
	}
	
	/**
	 * @return the yAxis
	 */
	public Object getyAxis() {
		return yAxis;
	}
	/**
	 * @param yAxis the yAxis to set
	 */
	public void setyAxis(Object yAxis) {
		this.yAxis = yAxis;
	}
	/** 
	 * @param status
	 * @see com.polaris_comm.dto.BaseDto#setStatus(java.lang.Integer)
	 * @时间: 2017年8月22日 下午2:41:30 
	 * @author: XuChuanHou
	*/
	@Override
	public void setStatus(Integer status) {
		super.setStatus(status);
		if(status!=null&&0!=status){
			this.setMsgDetail("");
			this.setMsgType(ResultDto.MSGTYPE_DANGER);
		}else{
			this.setMsgDetail("");
			this.setMsgType(ResultDto.MSGTYPE_SUCCESS);
		}
	}
	/** 
	 * @return
	 * @see com.polaris.ResultDto.dto.ResponseDto#toJSON()
	 * @时间: 2017年8月22日 下午2:06:54 
	 * @author: XuChuanHou
	*/
	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject=super.toJSON();
		if(jsonObject.containsKey("ydata")&&jsonObject.get("ydata")!=null){
			jsonObject.replace("datas", jsonObject.get("ydata"));
			jsonObject.remove("ydata");
		}
		return jsonObject;
	}
	
	@Override
	public JSONObject toJSON(BaseDto dto) {
		JSONObject jsonObject=super.toJSON(dto);
		if(jsonObject.containsKey("ydata")&&jsonObject.get("ydata")!=null){
			jsonObject.replace("datas", jsonObject.get("ydata"));
			jsonObject.remove("ydata");
		}
		return jsonObject;
	}
	
}
