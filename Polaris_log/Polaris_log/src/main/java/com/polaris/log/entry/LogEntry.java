package com.polaris.log.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.log.api.dto.LogDto;
import com.polaris.log.api.service.LogService;

@Service
public class LogEntry implements LogService {

	private static LogUtil logger = LogUtil.getInstance(LogEntry.class, false);
	
	@Autowired
	private com.polaris.log.service.LogService logService; 
	
	//线程数
	private ThreadPoolExecutor threadPool;
			
	//阻塞队列
	private volatile LinkedBlockingQueue<LogDto> queue;
	
	@PostConstruct //初始化方法的注解方式  
    public void init(){ 
		
		//启动线程数
		String threads = ConfClient.get("log.executor.threads", String.valueOf(Runtime.getRuntime().availableProcessors()));
		threadPool = new ThreadPoolExecutor(Integer.parseInt(threads), Integer.parseInt(threads), 
				10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		
		//创建阻塞队列大小
		String maxQueueSize = ConfClient.get("log.queue.maxsize", "500000");
		queue = new LinkedBlockingQueue<LogDto>(Integer.parseInt(maxQueueSize));
		
		//开启线程数量
		for (int i0 = 0; i0 < Integer.parseInt(threads); i0++) {
			consumeLog();
		}

	}
	
	//处理数据
	private void consumeLog() {
		threadPool.submit(new Runnable() {
			@Override
            public void run()  {
				
				//对象初始化
				List<LogDto> logDtoList = new ArrayList<>();
				
				//循环队列
				while(true) {
					
					//获取数据
					try {
						
						//如果持续1秒没有新日志产生，就把logDtoList里面的日志全部插入数据库
						LogDto dto = queue.poll(1000, TimeUnit.MILLISECONDS);
						if (dto == null) {
							if (logDtoList.size() > 0) {
								logService.insert(logDtoList);
								logDtoList.clear();
							}
							logDtoList.add(queue.take());
						} else {
							//每100条插一次数据库
							logDtoList.add(dto);
							if (logDtoList.size() >= 100) {
								logService.insert(logDtoList);
								logDtoList.clear();
							}
						}
					} catch (InterruptedException e) {
						logger.error("获取日志异常",e);
						Thread.currentThread().interrupt();
					}				
				}
            }
        });		
	}
	
	/**  
	 * @desc  : 日志插入
	 * @author: Yang Hao 
	 * @date  : 2017年11月10日 上午9:35:55 
	 * @param logDto
	 * @return 
	*/
	@Override
	public void insert(LogDto logDto) {
		
		//接入数据
		boolean result = false;
		try {
			result = queue.offer(logDto);
		} catch (Exception ex) {
			
			//直接抛弃日志
			logger.error("日志写入异常:",logDto.toString());
			return;
		}
		if (!result) {
			//直接抛弃日志
			logger.error("超过最大可以接受的日志数量"+ConfClient.get("log.queue.maxsize", "500000")+"直接抛弃：",logDto.toString());
			return;
		}
	}
	
	/**  
	 * @desc  : 日志查询
	 * @author: Yang Hao 
	 * @date  : 2017年11月10日 上午9:35:55 
	 * @param logDto
	 * @return 
	*/
	@Override
	public List<LogDto> query(LogDto dto) {
		return logService.query(dto);
	}
	
}
