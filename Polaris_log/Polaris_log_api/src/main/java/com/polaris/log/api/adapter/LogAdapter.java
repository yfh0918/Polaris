package com.polaris.log.api.adapter;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.SpringUtil;
import com.polaris.log.api.dto.LogDto;
import com.polaris.log.api.service.LogService;

/**   
 *
 * LogServiceAdapter.java 文件使用说明
 * 说明：XXXX<br/>
 *  
 *
 * @version ver 3.0.0
 * @author Shanghai Kinstar Tom.Yu Software .co.ltd. Yang Hao
 * @since 作成日期：2017年11月10日（Yang Hao）<br/>
 *        改修日期：
*/
@Component(value="logAdapter")
public class LogAdapter {

	//日志
	Logger logger = LoggerFactory.getLogger(LogAdapter.class);
	
	/** 
	* @Fields queue : 存放日志的队列
	*/ 
	private LinkedBlockingQueue<Map<String, Object>> queue = null;
	
	@PostConstruct
	public void consumeQueue(){
		
		//刷新时间间隔（默认30秒）
		long millis = Long.parseLong(ConfClient.get("log.refresh.millis", "30000"));
		
		//日志输出
		excuteLog(millis);
	}
	

	
	//执行日志
	private void excuteLog(long millis) {
		//进行判断
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					
					try {
						if (queue == null) {
							Thread.sleep(millis);
						} else {
							Map<String, Object> logMap = queue.take();
							LogDto dto = new LogDto();
							dto.setParameterMap(logMap,true);
							LogService logService = SpringUtil.getBean(LogService.class);
							logService.insert(dto);
						}
					} catch (InterruptedException e) {
						//存在中断异常，重新设置中断标识
						Thread.currentThread().interrupt();
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}
		}).start();
	}
	
	/**   
	 * @desc  : 获取队列，供comm调用
	 * @author: Yang Hao
	 * @date  : 2017年11月13日 上午10:10:44
	 * @return
	*/
	public LinkedBlockingQueue<Map<String, Object>> getBlockingQueue(){
		
		//是否输出日志
		if (StringUtil.isEmpty(ConfClient.get("log.queue.maxsize", ""))) {
			return null;
		}
		LogService logService = SpringUtil.getBean(LogService.class);
		if (logService == null) {
			return null;
		}
		
		//返回日志队列
		return queue;
	}
	

}
