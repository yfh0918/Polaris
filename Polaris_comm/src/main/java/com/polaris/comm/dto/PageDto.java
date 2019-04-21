package com.polaris.comm.dto;

import java.io.Serializable;

public class PageDto extends ResultDto implements Serializable {

	/**o
	 * 
	 */
	private static final long serialVersionUID = 1L;

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


}
