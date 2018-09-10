package com.polaris.log.api.dto;

import java.io.Serializable;

import com.polaris.comm.dto.BaseDto;

public class LogDto extends BaseDto implements Serializable {
	/** 
	* @Fields serialVersionUID : 
	*/ 
	private static final long serialVersionUID = 2652634865647491609L;
	private String uuid;
	/** 日志内容 */
	private String content;
	/** 操作方式 */
	private String logType;
	/** 操作IP地址 */
	private String remoteAddr;
	/** 操作人姓名 */ 
	private String yh_mc;
	/** 创建时间 */
	private String createDate;
	/** 请求服务的名称 */
	private String trace_id;
	private String parent_id;
	private String module_id;
	
	//供查询使用
	private String startDate;
	private String endDate;
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the logtype
	 */
	public String getLogType() {
		return logType;
	}
	/**
	 * @param logtype the logtype to set
	 */
	public void setLogType(String logType) {
		this.logType = logType;
	}
	/**
	 * @return the remoteAddr
	 */
	public String getRemoteAddr() {
		return remoteAddr;
	}
	/**
	 * @param remoteAddr the remoteAddr to set
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	/**
	 * @return the yh_mc
	 */
	public String getYh_mc() {
		return yh_mc;
	}
	/**
	 * @param yh_mc the yh_mc to set
	 */
	public void setYh_mc(String yh_mc) {
		this.yh_mc = yh_mc;
	}
	/**
	 * @return the createDate
	 */
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the parent_id
	 */
	public String getParent_id() {
		return parent_id;
	}
	/**
	 * @param parent_id the parent_id to set
	 */
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	/**
	 * @return the module_id
	 */
	public String getModule_id() {
		return module_id;
	}
	/**
	 * @param module_id the module_id to set
	 */
	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}
	
	public String getTrace_id() {
		return trace_id;
	}
	public void setTrace_id(String trace_id) {
		this.trace_id = trace_id;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	@Override
	public String toString() {
		return "LogDto [uuid=" + uuid + ", content=" + content + ", logType=" + logType + ", remoteAddr=" + remoteAddr
				+ ", yh_mc=" + yh_mc + ", createDate=" + createDate + ", trace_id=" + trace_id + ", parent_id="
				+ parent_id + ", module_id=" + module_id + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}
	
}
