package com.polaris.comm.supports;

import java.util.List;
import java.util.Map;

import com.polaris.comm.util.JDBCUtil;
import com.polaris.comm.util.NetUtils;

public class TimerSupport {

	public static String getToken(String uri) {
		return getToken(uri, NetUtils.LOCALHOST);
	}
	
	public static String getToken(String uri, String host) {
		String sql = "select token from s_sys_sso_tbl where uri = '"+uri+"' and ip_addr = '" +host+ "' and yxbz='1'";
		List<Map<String, Object>> mapList = JDBCUtil.getQueryResult(sql);
		if (mapList != null && mapList.size() > 0) {
			Map<String, Object> result = mapList.get(0);
			return result.get("token") == null ? result.get("TOKEN").toString() : result.get("token").toString();
		}
		return null;
	}
}
