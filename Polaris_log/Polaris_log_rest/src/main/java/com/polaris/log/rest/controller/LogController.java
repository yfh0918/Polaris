package com.polaris.log.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.polaris.log.api.dto.LogDto;
import com.polaris.log.api.service.LogService;

/**
 * 日志模块
 *
 * @return
 */
@Component
@Path("/rest/log")
public class LogController {

    @Reference(version = "1.0.0")
    private LogService logService;

    /**
     * 用户登入
     *
     * @return
     */
    @POST
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LogDto> query(@Context HttpServletRequest request) {
		LogDto logDto = new LogDto();
		logDto.setStartDate(request.getParameter("startDate"));
		logDto.setEndDate(request.getParameter("endDate"));
		logDto.setTrace_id(request.getParameter("trace_id"));
		logDto.setParent_id(request.getParameter("parent_id"));
		logDto.setModule_id(request.getParameter("module_id"));
		logDto.setContent(request.getParameter("content"));
		logDto.setLogType(request.getParameter("logType"));
		List<LogDto> logDtos = logService.query(logDto);
		return logDtos;
    }
    
    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public void query(){
    	LogDto logDto = new LogDto();
    	logService.insert(logDto);
    }
}
