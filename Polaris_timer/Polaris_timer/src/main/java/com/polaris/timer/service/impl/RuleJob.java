package com.polaris.timer.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;
import com.polaris.core.connect.HttpClientSupport;
import com.polaris.timer.api.dto.TimerDto;

//Job执行
public class RuleJob implements Job {

    //日志
    private final LogUtil logger = LogUtil.getInstance(RuleJob.class);

    //执行
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
    	TimerDto plan = (TimerDto) arg0.getJobDetail().getJobDataMap().get("plan");
        if (plan == null) {
        	return;
        }
        
        try {
            //构造参数
            Map<String, Object> headerParameter = new HashMap<>();
            headerParameter.put(Constant.USER_TOKEN, plan.getToken());
            headerParameter.put(Constant.REQUEST_TYPE, Constant.TOKEN_USER_TYPE);

            logger.info(plan.getName() + "开始执行");
            String result = HttpClientSupport.doPost((String) plan.getUrl(),  headerParameter);
            logger.info(plan.getName() + "执行结果：" + result);
            
        } catch (Exception e) {
            logger.error(plan.getName() + "定时调用异常:", e);
        }
        logger.info(plan.getName() + "执行结束");
    }
}
