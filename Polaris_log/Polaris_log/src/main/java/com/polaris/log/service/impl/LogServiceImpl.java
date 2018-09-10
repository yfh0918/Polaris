package com.polaris.log.service.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.polaris.comm.util.LogUtil;
import com.polaris.log.api.dto.LogDto;
import com.polaris.log.service.LogService;

@Service
public class LogServiceImpl implements LogService {

	public static final String LOG_TYPE_STR = "logType";
	public static final String CREATE_DATE_STR = "createDate";
	public static final String PARENT_ID_STR = "parent_id";
	public static final String TRACE_ID_STR = "trace_id";
	public static final String MODULE_ID_STR = "module_id";
	public static final String CONTENT_STR = "content";

	public static final String YEAR_MONTH = "yyyyMM";
	public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
	public static final String YEAR_MONTH_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
	
	//模糊查询的前缀
	public static final String LIKE_SUFFIX = "^.*";
	//模糊查询的后缀
	public static final String LIKE_PREFIX = ".*$";

	private static final String[] LOG_TYPE = { "info", "debug", "error", "warn", "trace" };

	private static LogUtil logger = LogUtil.getInstance(LogServiceImpl.class, false);

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * @desc : 保存日志,使用日志产生时间的年月为collection的名称
	 * @author: Yang Hao
	 * @date : 2017年11月9日 下午5:07:29
	 * @param dto
	 * @return
	 */
	public void insert(LogDto logDto) {
		SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH);
		String collectionName = sdf.format(new Date());
		mongoTemplate.insert(logDto, collectionName);
	}
	
	/**
	 * @desc : 保存日志,使用日志产生时间的年月为collection的名称
	 * @author: Yang Hao
	 * @date : 2017年11月9日 下午5:07:29
	 * @param dtoList
	 * @return
	 */
	public void insert(List<LogDto> logDtos) {
		SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH);
		if (CollectionUtils.isEmpty(logDtos)) {
			return;
		}
		String collectionName = sdf.format(new Date());
		mongoTemplate.insert(logDtos, collectionName);
	}


	/**
	 * @desc : 查询记录
	 * 		   当查询开始时间和结束时间不在同一个月内，那么第一个Collection的查询的开始时间为前台传来的开始时间拼接“ 00:00:00”,结束时间为开始时间所在的月的最后一天；
	 * 		   最后一个Collection的查询开始时间为前台传来的结束时间 所在月的第一天 ，结束时间为前台传来的结束时间拼接“ 23:59:59”
	 *       其他的Collection的查询都不涉及时间的查询
	 * @author: Yang Hao
	 * @date : 2017年11月10日 下午2:44:31
	 * @return
	 */
	public List<LogDto> query(LogDto dto) {
		// 日志类型：flag=false 为查询全部日志类型
		boolean flag = false;
		for (String type : LOG_TYPE) {
			if (type.equals(dto.getLogType())) {
				flag = true;
				break;
			}
		}
		
		List<LogDto> logDtos = new ArrayList<>();
		List<String> collectionNames = getCollectionNames(dto);

		for (int i = 0; i < collectionNames.size(); i++) {
			Criteria criteria = new Criteria();
			String collectionName = collectionNames.get(i);
			
			if (flag) {
				criteria = where(LOG_TYPE_STR).is(dto.getLogType());
			} else {
				criteria = where(LOG_TYPE_STR).nin(dto.getLogType());
			}
			// parentId模糊匹配
			if (StringUtils.isNotEmpty(dto.getParent_id())) {
				Pattern pattern = Pattern.compile(LIKE_PREFIX + dto.getParent_id() + LIKE_SUFFIX);
				criteria.and(PARENT_ID_STR).regex(pattern);
			}

			// traceId模糊匹配
			if (StringUtils.isNotEmpty(dto.getParent_id())) {
				Pattern pattern = Pattern.compile(LIKE_PREFIX + dto.getTrace_id() + LIKE_SUFFIX);
				criteria.and(TRACE_ID_STR).regex(pattern);
			}

			// moduleId模糊匹配
			if (StringUtils.isNotEmpty(dto.getModule_id())) {
				Pattern pattern = Pattern.compile(LIKE_PREFIX + dto.getModule_id() + LIKE_SUFFIX);
				criteria.and(MODULE_ID_STR).regex(pattern);
			}

			// content模糊匹配
			if (StringUtils.isNotEmpty(dto.getContent())) {
				Pattern pattern = Pattern.compile(LIKE_PREFIX + dto.getContent() + LIKE_SUFFIX);
				criteria.and(CONTENT_STR).regex(pattern);
			}
			
			//说明查询开始时间和结束时间在同一个月内
			if(collectionNames.size() == 1){
				String startDate = new StringBuffer().append(dto.getStartDate()).append(" 00:00:00").toString();
				String endDate = new StringBuffer().append(dto.getEndDate()).append(" 23:59:59").toString();
				criteria.andOperator(Criteria.where(CREATE_DATE_STR).gte(startDate), Criteria.where(CREATE_DATE_STR).lt(endDate));
			}else{
				//第一个月的开始时间用前台传来的开始时间
				if (i == 0) {
					String startDate = new StringBuffer().append(dto.getStartDate()).append(" 00:00:00").toString();
					String endDate = getLastDay(collectionName);
					criteria.andOperator(Criteria.where(CREATE_DATE_STR).gte(startDate), Criteria.where(CREATE_DATE_STR).lt(endDate));
				} 
				//最后一个月的结束时间用前台传来的结束时间
				else if (i == collectionNames.size() - 1) {
					String startDate = getFirstDay(collectionName);
					String endDate = new StringBuffer().append(dto.getEndDate()).append(" 23:59:59").toString();
					criteria.andOperator(Criteria.where(CREATE_DATE_STR).gte(startDate), Criteria.where(CREATE_DATE_STR).lt(endDate));
				}
			}
			
			// 查询
			Query query = new Query(criteria);
			List<LogDto> list = mongoTemplate.find(query, LogDto.class, collectionName);
			logDtos.addAll(list);
		}
		return logDtos;
	}

	/**
	 * @desc 获取collectionName
	 * 
	 * @param logDto
	 * @return
	 */
	private List<String> getCollectionNames(LogDto logDto) {
		List<String> collectionNames = new ArrayList<>();
		
		//格式化为：年-月-日  时:分:秒
		SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY);
		try {
			Calendar max = Calendar.getInstance();
			Calendar min = Calendar.getInstance();
			min.setTime(sdf.parse(logDto.getStartDate()));
			max.setTime(sdf.parse(logDto.getEndDate()));
			
			//格式化为：年月
			sdf = new SimpleDateFormat(YEAR_MONTH);
			
			//跨月的情况
			while (min.before(max)) {
				String collectionName = sdf.format(min.getTime());
				collectionNames.add(collectionName);
				min.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			logger.error("Date parse error", e);
		}
		
		return collectionNames;
	}

	/**
	 * 获取一个月的第一天
	 * 
	 * @param yearMonth 例如：201701
	 * @return
	 * @throws ParseException
	 */
	private String getFirstDay(String yearMonth) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(yearMonth));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			
			//例如：2017-01-01
			sdf = new SimpleDateFormat(YEAR_MONTH_DAY);
			String yearMonthDay = sdf.format(calendar.getTime());
			
			//在年月日上拼接上00:00:00
			return new StringBuffer().append(yearMonthDay).append(" 00:00:00").toString();
			
		} catch (ParseException e) {
			logger.error("Date parse error", e);
		}
		return null;
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param yearMonth
	 * @return
	 */
	private String getLastDay(String yearMonth) {
		String date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(yearMonth));
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			//例如：2017-01-01
			sdf = new SimpleDateFormat(YEAR_MONTH_DAY);
			date = sdf.format(calendar.getTime());
			
			//在年月日上拼接上 23:59:59
			date = new StringBuffer().append(date).append(" 23:59:59").toString();
		} catch (ParseException e) {
			logger.error("Date parse error", e);
		}
		return date;
	}
}
