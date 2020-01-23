package com.polaris.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.polaris.core.Constant;
import com.polaris.core.GlobalContext;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.UuidUtil;

/**
 * 实现的第一个过滤器
 * 用于初始化一些参数，变量等信息
 *
 * @return 
 */
public class RequestFirstFilter implements Filter {
	
	@Override
    public void destroy() {
    }

	@Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
		GlobalContext.setContext(GlobalContext.REQUEST, request);
		if (StringUtil.isNotEmpty(((HttpServletRequest)request).getHeader(GlobalContext.TRACE_ID))) {
			GlobalContext.setContext(GlobalContext.TRACE_ID, ((HttpServletRequest)request).getHeader(GlobalContext.TRACE_ID));
		} else {
			GlobalContext.setContext(GlobalContext.TRACE_ID, UuidUtil.generateUuid());
		}
		GlobalContext.setContext(GlobalContext.MODULE_ID, GlobalContext.getModuleId());
		if (StringUtil.isNotEmpty(((HttpServletRequest)request).getHeader(GlobalContext.PARENT_ID))) {
			GlobalContext.setContext(GlobalContext.PARENT_ID, ((HttpServletRequest)request).getHeader(GlobalContext.PARENT_ID));
		}
		try {
			request.setCharacterEncoding(ConfClient.get("encoding",Constant.UTF_CODE));
	        response.setCharacterEncoding(ConfClient.get("encoding",Constant.UTF_CODE));
	        chain.doFilter(request, response);
		} finally {
			GlobalContext.removeContext();
		}
    }

	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}


