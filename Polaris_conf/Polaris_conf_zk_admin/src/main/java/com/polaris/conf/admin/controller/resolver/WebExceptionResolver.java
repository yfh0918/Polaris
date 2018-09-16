package com.polaris.conf.admin.controller.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.polaris.comm.util.LogUtil;
import com.polaris.conf.admin.core.util.JacksonUtil;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * common exception resolver
 */
public class WebExceptionResolver implements HandlerExceptionResolver {
	private static transient LogUtil logger = LogUtil.getInstance(WebExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		logger.error("system catch exception:{}", ex);
		
		ModelAndView mv = new ModelAndView();
		HandlerMethod method = (HandlerMethod)handler;
		ResponseBody responseBody = method.getMethodAnnotation(ResponseBody.class);
		if (responseBody != null) {
			response.setContentType("application/json;charset=UTF-8");
			mv.addObject("result", JacksonUtil.writeValueAsString(new ReturnT<String>(500, ex.toString().replaceAll("\n", "<br/>"))));
			mv.setViewName("/common/common.result");
		} else {
			mv.addObject("exceptionMsg", ex.toString().replaceAll("\n", "<br/>"));	
			mv.setViewName("/common/common.exception");
		}
		return mv;
	}
	
}