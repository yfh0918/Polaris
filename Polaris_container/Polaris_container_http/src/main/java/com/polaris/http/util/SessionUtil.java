package com.polaris.http.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.polaris.comm.Constant;


public class SessionUtil {
	private SessionUtil() {
	}
	public static String getUserIdBySceneType(HttpServletRequest request, String secenType) {
		String userId = null;
		if(StringUtils.isBlank(secenType)){
			return userId;
		}
		switch (secenType) {
		case "0":
			userId = null;
			break;
		case "1":
		case "2":
			userId = getUserIdFromSession(request);
			break;
		default:
			break;
		}
		return userId;
	}
	
	public static String getUserIdFromSession(HttpServletRequest request){
		if(request.getSession()!=null&&request.getSession().getAttribute(Constant.SESSION_USER_ID)!=null){
			return request.getSession().getAttribute(Constant.SESSION_USER_ID).toString();
		}
		return null;
		
	}
	
	public static String getUserId(HttpServletRequest request){
		if(request.getSession()!=null&&request.getSession().getAttribute(Constant.SESSION_USER_ID)!=null){
			return request.getSession().getAttribute(Constant.SESSION_USER_ID).toString();
		}
		return null;
	}

}
