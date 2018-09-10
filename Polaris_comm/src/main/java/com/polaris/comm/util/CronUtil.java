package com.polaris.comm.util;

public class CronUtil {

	/**
	 * 返回执行Cron
	 * @param jhlx：计划类型（d：日计划      w：周计划       m：月计划）
	 * @param jh_z：周, jh_r:日， jh_s：时，jh_f：分，jh_m：秒
	 * @return Cron
	 */
	public static String getCron(String jhlx, 
								 String jh_z, 
								 String jh_r, 
								 String jh_s, 
								 String jh_f, 
								 String jh_m) {
		
		//日计划
		if (jhlx != null && "d".equals(jhlx)) {
			return jh_m + " " + jh_f + " " + jh_s + " * * ?";
			
		//周计划
		} else if (jhlx != null && "w".equals(jhlx)) {
			switch (jh_z) {
				case "1" : return jh_m + " " + jh_f + " " + jh_s + " ? * MON";
				case "2" : return jh_m + " " + jh_f + " " + jh_s + " ? * TUE";
				case "3" : return jh_m + " " + jh_f + " " + jh_s + " ? * WED";
				case "4" : return jh_m + " " + jh_f + " " + jh_s + " ? * THU";
				case "5" : return jh_m + " " + jh_f + " " + jh_s + " ? * FRI";
				case "6" : return jh_m + " " + jh_f + " " + jh_s + " ? * SAT";
				case "7" : return jh_m + " " + jh_f + " " + jh_s + " ? * SUN";
				default :
					break;
			}

		//月计划
		} else if (jhlx != null && "m".equals(jhlx)) {
			return jh_m + " " + jh_f + " " + jh_s + " " + jh_r +" * ?";
		}
		
		return null;
	}
}
