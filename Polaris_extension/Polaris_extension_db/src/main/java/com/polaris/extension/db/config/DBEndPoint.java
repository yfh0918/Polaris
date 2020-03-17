package com.polaris.extension.db.config;

import java.util.ArrayList;
import java.util.List;

import com.polaris.core.config.ConfEndPoint;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.util.StringUtil;

public class DBEndPoint implements ConfEndPoint {
	
	private static List<String> nameList = new ArrayList<>();
	private static String endfix = ".url";
	private static String prefix1 = "jdbc.";
	private static String prefix2 = "spring.datasource.";
	
	@Override
	public void onChange(String sequence, Object keyObj, Object value, Opt opt) {
		String key = keyObj.toString();
		if (StringUtil.isNotEmpty(key) && key.endsWith(endfix)) {
			if (key.startsWith(prefix1) && key.length() > 8) {
				nameList.add(key.substring(5,key.length() - 4));
			} else if (key.startsWith(prefix2)&& key.length() > 21) {
				nameList.add(key.substring(18,key.length() - 4));
			}
		}
	}
	
	public static List<String> getNames() {
		return nameList;
	}
	
}
