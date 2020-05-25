package com.polaris.demo;


import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.log.Log4jCallBack;


public class DemoLog4jCallBack implements Log4jCallBack
{
	private static final Logger logger = LoggerFactory.getLogger(DemoLog4jCallBack.class);
	public void call(Level level, String message, Object... args){
		System.out.println("call level_0");
		//不能在回调函数中调用logger.xx会造成死循环
		//logger.info("call level_0");
	}
	public void call(Level level, String message, Throwable t) {
		System.out.println("call level_1");
	}
}
