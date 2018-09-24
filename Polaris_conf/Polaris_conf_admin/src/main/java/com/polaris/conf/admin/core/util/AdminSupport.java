package com.polaris.conf.admin.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.polaris.comm.config.ConfigHandlerProvider;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.StringUtil;
import com.polaris.conf.admin.Constant;

public class AdminSupport {
	private static LogUtil logger = LogUtil.getInstance(AdminSupport.class);
	//载入配置
	@SuppressWarnings("unchecked")
	public static boolean loadProperties() {
		
		try {
			
			// namespace
			String path = PropertyUtils.getFilePath(Constant.CONSTANT_NAME_SPACE);
			File root = new File(path);
			if (!root.exists() || !root.isDirectory()) {
				logger.error(path + "is not exist !");
				return false;
			}
			boolean isLoad = false;
			File[] namespacefiles = root.listFiles();
			for (File temp : namespacefiles) {
				if (temp.isDirectory()) {
					
					//group
					String name1 = temp.getName();
					String namespacepath = PropertyUtils.getFilePath(Constant.CONSTANT_NAME_SPACE + File.separator + name1);
					File namespacefile = new File(namespacepath);
					File[] groupfiles = namespacefile.listFiles();
					for (File temp2 : groupfiles) {
						if (temp2.isDirectory()) {
							
							//key
							String name2 = temp2.getName();
							String grouppath = PropertyUtils.getFilePath(Constant.CONSTANT_NAME_SPACE + File.separator + name1 + File.separator + name2);
							File groupfile = new File(grouppath);
							File[] keyfiles = groupfile.listFiles();
							for (File keyfile : keyfiles) {
								if (keyfile.isFile() && keyfile.getName().endsWith(".properties")) {
									   PropertiesConfiguration config  = new PropertiesConfiguration(keyfile);
									   config.setEncoding(Constant.UTF_CODE);
									   Iterator<String> ite = config.getKeys();
									   while (ite.hasNext()) {
										   isLoad = true;
										   String key = ite.next();
										   String value = ConfigHandlerProvider.getInstance().getKey(name1, name2, key, false);
										   if (StringUtil.isEmpty(value)) {
											   Object object = config.getProperty(key);
											   if (object == null) {
												   continue;
											   }
											   //list
											   String tempV = null;
											   if (object instanceof ArrayList) {
												   StringBuilder strB = new StringBuilder();
												   for (String tempr : ((List<String>)object)) {
													   if (StringUtil.isEmpty(strB.toString())) {
														   strB.append(tempr);
													   } else {
														   strB.append(",");
														   strB.append(tempr);
													   }
												   }
												   tempV = strB.toString();
											   } else {
												   tempV = object.toString();
											   }
											   ConfigHandlerProvider.getInstance().addKey(name1, name2, key, tempV, false);
										   }
									   }
								}
							}
						}
					}

				}
			}
			if (!isLoad) {
				logger.error(path + "is not correct !");
				return false;
			}
			
			
			
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public static List<String> getAllNameSpaces() {
		List<String> namespaceList = ConfigHandlerProvider.getInstance().getAllNameSpaces(false);
		if (namespaceList == null) {
			namespaceList = new ArrayList<>();
		}
		return namespaceList;
	}
	
	public static void addNameSpace(String namespace) {
		ConfigHandlerProvider.getInstance().addNameSpace(namespace, false);
	}
	
	public static boolean deleteNameSpace(String namespace) {
		return ConfigHandlerProvider.getInstance().deleteNameSpace(namespace, false);
	}
	
	public static List<String> getAllGroups(String namespace) {
		List<String> groups = ConfigHandlerProvider.getInstance().getAllGroups(namespace, false);
		if (groups == null) {
			groups = new ArrayList<>();
		}
		return groups;
	}
	
	public static boolean addGroup(String namespace, String group) {
		return ConfigHandlerProvider.getInstance().addGroup(namespace, group, false);
	}
	
	public static boolean deleteGroup(String namespace, String group) {
		return ConfigHandlerProvider.getInstance().deleteGroup(namespace, group, false);
	}
	
	public static List<String> getAllKeys(String namespace, String group) {
		List<String> keys = ConfigHandlerProvider.getInstance().getAllKeys(namespace,group, false);
		if (keys == null) {
			keys = new ArrayList<>();
		}
		return keys;
	}
}
