package com.polaris.comm.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class BaseDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 属于组件:all 作用：判断返回的操作是否成功 标准值：0-处理正常,1-处理失败,2-后台出错,3-权限不足,4-认证失败   前台是否传输：否 后台是否传输：是
	 */
	private Integer status;

	/**
	 * 属于组件:table 作用：从第X条数据开始 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Integer pageFrom;

	/**
	 * 属于组件:table 作用：到第X条数据结束 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Integer pageTo;

	/**
	 * 属于组件:table 作用：第几页 标准值： 前台是否传输：是 后台是否传输：是
	 */
	private Integer pageIndex;

	/**
	 * 属于组件:table 作用：总页数 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Integer totalPage;

	/**
	 * 属于组件:table 作用：总共多少条数据 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Integer total;

	/**
	 * 属于组件:table 作用：每页显示多少条 标准值： 前台是否传输：是 后台是否传输：是
	 */
	private Integer pageSize;

	/**
	 * 属于组件:table 作用：每页显示多少条的选择项 标准值： 前台是否传输：否 后台是否传输：否
	 */
	private List<Integer> paging;


	/**
	 * 属于组件:tree 作用：前台向后台传输此值,用于树点击展开时展开的id值 标准值： 前台是否传输：是 后台是否传输：是
	 */
	private String extendid;

	/**
	 * 属于组件:table 作用：排序的字段名称 标准值： 前台是否传输：是 后台是否传输：是
	 */
	private String order;

	/**
	 * 属于组件:table 作用：排序的type 标准值：desc(降序),asc(升序) 前台是否传输：是 后台是否传输：是
	 */
	private String orderType;
	
	/**
	 * 属于组件:table 作用：排序的字段名称 标准值： 前台是否传输：是 后台是否传输：是
	 */
	private String sort;
	
	/**
	 * 属于组件:table 作用：排序的type 标准值：desc(降序),asc(升序) 前台是否传输：是 后台是否传输：是
	 */
	private String sortType;
	
	/**
	 * 属于组件:  作用：自定义单个data 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private Map<String, Object> data;
	
	/**
	 * 属于组件: table,tree 作用：tree与table的显示行数据,内在数据字段根据显示自定义 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private List<Map<String, Object>> datas;

	/**
	 * 属于组件:msg_box 作用： 标准值：danger,info,success,warning 前台是否传输：否 后台是否传输：是
	 */
	private String msgType;
	
	/**
	 * 属于组件:msg_box 作用：小型msgbox的主要文字,大型的上行文字 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private String msgContent;

	/**
	 * 属于组件:msg_box 作用：大型msgbox的下行文字 标准值： 前台是否传输：否 后台是否传输：是
	 */
	private String msgDetail;

	/**
	 * 属于组件:table 作用：下一页还有几条，前台是否传输：否 后台是否传输：是
	 */
	private Integer leftover;
	
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
		setParameterMap(parameterMap, false);
	}
	public void setParameterMap(Map<String, Object> parameterMap, boolean isLoad) {
		this.parameterMap = parameterMap;
		if (isLoad) {
			//载入map
		    for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
	    		try {
        			BeanUtils.setProperty(this, entry.getKey(), entry.getValue());
	    		} catch (Exception ex) {
					//nothing
	        	}
		    }
		}
	}
	
	 public  BaseDto(){
		 
	 }
	 
	 public  BaseDto(Integer status,String msgContent){
	    	this.status = status;
	    	this.msgContent=msgContent;
	  }
	

	public Integer getStatus() {
		if(null==status){
			status=0;
		}
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public Integer getPageFrom() {
		if (null == pageFrom){
			pageFrom = 1;
		}else if(total>0){
			pageFrom =pageFrom +1;
		}
		return pageFrom;
	}

	public void setPageFrom(Integer pageFrom) {
		this.pageFrom = pageFrom;
	}

	public Integer getPageTo() {
		if (null == pageTo){
			pageTo = 10;
		}else if(pageIndex != null && pageIndex.equals(totalPage)){
			pageTo=total;
		}
		return pageTo;
	}

	public void setPageTo(Integer pageTo) {
		this.pageTo = pageTo;
	}

	public Integer getPageIndex() {
		if (null == pageIndex) {
			pageIndex = 1;
		}
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getTotalPage() {
		if (null == totalPage) {
			totalPage = 0;
		}
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getTotal() {
		if (null == total) {
			total = 0;
		}
		return total;
	}

	public void setTotal(Integer total) {
		if(total==null||total==0){
			this.totalPage = 1;
			this.total =0;
		}else{
			this.total = total;
			Integer count = 0;
			if (total <= 0||this.getPageSize()==null||this.getPageSize()<=0) {
				this.totalPage = 1;
			} else {
				count = total / this.getPageSize();
				if (total % this.getPageSize() > 0) {
					count++;
				}
				this.totalPage = count;
			}
		}
	}

	public Integer getPageSize() {
		if (null == pageSize) {
			pageSize = 10;
		}
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<Integer> getPaging() {
		return paging;
	}

	public void setPaging(List<Integer> paging) {
		this.paging = paging;
	}

	public String getExtendid() {
		return extendid;
	}

	public void setExtendid(String extendid) {
		this.extendid = extendid;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

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

	public Integer getLeftover() {
		return leftover;
	}

	public void setLeftover(Integer leftover) {
		this.leftover = leftover;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgDetail() {
		return msgDetail;
	}

	public void setMsgDetail(String msgDetail) {
		this.msgDetail = msgDetail;
	}

}
