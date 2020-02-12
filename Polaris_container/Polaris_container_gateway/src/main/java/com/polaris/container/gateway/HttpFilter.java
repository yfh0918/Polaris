package com.polaris.container.gateway;

import com.polaris.core.dto.ResultDto;

import io.netty.handler.codec.http.HttpResponseStatus;

@SuppressWarnings("rawtypes")
public abstract class HttpFilter extends HttpFilterOrder {
	/**
     * 中途被拦截需要返回的信息
     *
     */
	private ResultDto resultDto;
	public ResultDto getResultDto() {
		return resultDto;
	}

	public void setResultDto(ResultDto resultDto) {
		this.resultDto = resultDto;
	}
	
	private HttpResponseStatus status = HttpResponseStatus.FORBIDDEN;
	public HttpResponseStatus getStatus() {
		return status;
	}
	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}

}
