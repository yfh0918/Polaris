package com.polaris.core.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberUtil {

	private static final Logger logger =  LoggerFactory.getLogger(NumberUtil.class);
	
	private NumberUtil() {
	}
	
	public static Double convertDouble(Object value) {
		if (value == null) {
			return 0.00;
		}
		try {
			return Double.parseDouble(value.toString());
		} catch (Exception ex) {
			logger.error("NumberUtil.convertDouble",ex);
			return 0.00;
		}
	}
	
	public static BigDecimal convertBigDecimal(Object value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(value.toString());
		} catch (Exception ex) {
			logger.error("NumberUtil.convertBigDecimal",ex);
			return BigDecimal.ZERO;
		}
	}
	
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception ex) {
			return false;
		}   
	}
}
