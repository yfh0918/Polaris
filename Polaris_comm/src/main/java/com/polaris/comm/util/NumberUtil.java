package com.polaris.comm.util;

import java.math.BigDecimal;

public class NumberUtil {

	private static final LogUtil logger =  LogUtil.getInstance(NumberUtil.class);
	
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
