package com.polaris.container.servlet.filter;

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
 * 用于追踪的filter
 *
 * @return 
 */
public class TraceFilter implements Filter {
	
	@Override
    public void destroy() {
    }

	@Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
		
		try {
			GlobalContext.setContext(GlobalContext.REQUEST, request);
			GlobalContext.setContext(GlobalContext.RESPONSE, response);
			
			//traceId
			String traceId = ((HttpServletRequest)request).getHeader(GlobalContext.TRACE_ID);
			if (StringUtil.isEmpty(traceId)) {
				GlobalContext.setTraceId(UuidUtil.generateUuid());
			}  else {
				GlobalContext.setTraceId(traceId);
			}
			
			//parentId
			String parentId = ((HttpServletRequest)request).getHeader(GlobalContext.SPAN_ID);
			if (StringUtil.isNotEmpty(parentId)) {
				GlobalContext.setParentId(parentId);
			} 
			
			//spanId
			GlobalContext.setSpanId(UuidUtil.generateUuid());
			
			//encoding
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


