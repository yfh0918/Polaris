package com.polaris.timer.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.alibaba.dubbo.config.annotation.Service;
import com.polaris.comm.Constant;
import com.polaris.comm.dto.ResultDto;
import com.polaris.comm.dto.StatusMsg;
import com.polaris.comm.thread.InheritableThreadLocalExecutor;
import com.polaris.comm.util.FileUtil;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.ResultUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.timer.api.dto.TimerDto;
import com.polaris.timer.service.RuleTaskService;

@Service
public class RuleTaskServiceImpl implements RuleTaskService {

    //日志
    private static final LogUtil logger = LogUtil.getInstance(RuleTaskServiceImpl.class);
    
	//线程池
	ThreadPoolExecutor threadPool = new InheritableThreadLocalExecutor(1, 1, 
			0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    //错误信息
    private static final String[] SCHEDULER_MESSAGE = {
    		"参数为空",
            "无效的计划名称",
            "无效的计划时间",
            "无效的执行路径",
            "计划任务更新失败!",
            "计划任务持久化失败",
            "计划任务加载失败",
            "token不能为空"};

    //执行计划的变量参数
    private static final String STR_FALSE = "false";
    private static final String PLAN_NAME = "plan";
    private static final String TRIGGER_NAME_PREX = "trigger";
    private static final String SERIALISE_FOLDER = "timer";
    private static final String SERIALISE_FILE_NAME = "object.adt";
	
    //执行计划
    private Scheduler scheduler = null;

    /**
     * @param dto
     * @return
     * @desc : 更新计划任务
     * @author: Yang Hao
     * @date : 2017年10月17日 下午3:46:03
     */
    public ResultDto updateScheduler(TimerDto dto) {

    	//检查参数
    	if (dto == null) {
    		StatusMsg statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[0]);
            return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    	}
    	if (StringUtil.isEmpty(dto.getName())) {
    		StatusMsg statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[1]);
            return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    	}
    	if (StringUtil.isEmpty(dto.getSchedule())) {
    		StatusMsg statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[2]);
            return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    	}
    	if (StringUtil.isEmpty(dto.getUrl())) {
    		StatusMsg statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[3]);
            return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    	}
    	if (StringUtil.isEmpty(dto.getToken())) {
    		StatusMsg statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[7]);
            return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    	}
        
        //查询存在的计划任务
        StatusMsg statmsg = new StatusMsg(Constant.STATUS_SUCCESS, Constant.MESSAGE_UPDATE_SUCCESS);
        try {
        	boolean isExist = false;
        	boolean isDel = false;
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP))) {
				
				//job名
                String jobName = jobKey.getName();
                if (StringUtil.isEmpty(jobName)) {
                    continue;
                }
                
                //job匹配
                if (jobName.equals(dto.getName())) {
                	isExist = true;
                	
                	//是否需要删除
                	if (STR_FALSE.equals(dto.getEnable())) {
                		scheduler.deleteJob(jobKey);
                		isDel = true;
                	} else {
                		
                		//获取计划的详细信息
                		TimerDto plan = (TimerDto) scheduler.getJobDetail(jobKey).getJobDataMap().get(PLAN_NAME);
                		if (!dto.getSchedule().equals(plan.getSchedule()) ||
                				!dto.getUrl().equals(plan.getUrl()) ||
                				!dto.getToken().equals(plan.getToken())) {
                			
                			//删除原有计划
                			scheduler.deleteJob(jobKey); 
                       		isDel = true;
                		}
                	}
                    break;
                }
			} 
			
			//新增新计划
			if (!STR_FALSE.equals(dto.getEnable())) {
				
				//不存在的计划需要增加
				if (!isExist) {
					excuteScheduler(dto);
				} else {
					
					//存在的计划，但是被删除的也要增加
					if (isDel) {
						excuteScheduler(dto);
					}
				}
			}

        }catch (SchedulerException e1) {
			logger.error(e1);
			statmsg = new StatusMsg(Constant.STATUS_FAILED, SCHEDULER_MESSAGE[4]);
		}
        
        //文件序列化
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                serialisePlans();
            }
        });

        //返回结果
        return ResultUtil.createResponseSaveOrUpdateJson(statmsg);
    }

    /**
     * @param dto
     * @return
     * @desc : 获取计划任务
     * @author: Yang Hao
     * @date : 2017年10月17日 下午1:55:58
     */
    @Override
    public List<TimerDto> findActivePlans(TimerDto dto) {

        //内存中的任务
        List<TimerDto> usePlans = new ArrayList<>();

        try {

        	//是否存在计划，没有直接返回
            if (scheduler == null || CollectionUtils.isEmpty(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP)))) {
                return null;
            }

            //查询内存中的所有的有效任务
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP))) {

                //job名
                String jobName = jobKey.getName();
                if (StringUtil.isEmpty(jobName)) {
                    continue;
                }
                TimerDto plan = (TimerDto) scheduler.getJobDetail(jobKey).getJobDataMap().get(PLAN_NAME);
                usePlans.add(plan);
				
                
            }
        } catch (SchedulerException ex) {
            logger.error(ex);
        }
        return usePlans;
    }

    //设置开始时间
	@PostConstruct
    public void loadPlans() {

        //执行计划
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            deSerialisePlans();
            scheduler.start();
        } catch (SchedulerException e) {
        	logger.error(SCHEDULER_MESSAGE[6], e);
        }
    }

    //执行计划
    private void excuteScheduler(TimerDto plan) throws SchedulerException {

    	//plan参数
        JobDataMap jMap = new JobDataMap();
        jMap.put(PLAN_NAME, plan);

        //执行具体的计划
        String expression = plan.getSchedule();
        String jobName = plan.getName();
        String triggerName = TRIGGER_NAME_PREX + jobName;
        JobDetail job = JobBuilder.newJob(RuleJob.class)
                .withIdentity(jobName, Scheduler.DEFAULT_GROUP)
                .usingJobData(jMap)
                .build();

        //触发器
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(expression)).build();

        //执行
        scheduler.scheduleJob(job, trigger);
    }
    
    //文件序列化
    private void serialisePlans() {
    	try {
    		List<TimerDto> plans = new ArrayList<>();
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP))) {
				
				//job名
			    String jobName = jobKey.getName();
			    if (StringUtil.isEmpty(jobName)) {
			        continue;
			    }
			    
				//获取计划的详细信息
				TimerDto plan = (TimerDto) scheduler.getJobDetail(jobKey).getJobDataMap().get(PLAN_NAME);
				plans.add(plan);
			}
			
			//持久化计划任务到磁盘
			if (plans.size() > 0) {
				FileUtil.createDirectory(PropertyUtils.getFilePath(SERIALISE_FOLDER));
				String path = PropertyUtils.getFilePath(SERIALISE_FOLDER + File.separator + SERIALISE_FILE_NAME);
				FileUtil.deleteFile(path);
				File file = FileUtil.makeFile(path);
				try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
		        	TimerDto[] obj = new TimerDto[plans.size()];
		            plans.toArray(obj);
		            out.writeObject(obj);
				}
			}
		} catch (SchedulerException | IOException e) {
			logger.error(SCHEDULER_MESSAGE[5], e);
		} 
    }
    
    //文件反序列化
    private void deSerialisePlans() {
    	try {
    		FileUtil.createDirectory(PropertyUtils.getFilePath(SERIALISE_FOLDER));
			String path = PropertyUtils.getFilePath(SERIALISE_FOLDER + File.separator + SERIALISE_FILE_NAME);
    		File file = new File(path);
    		if (!file.exists()) {
    			return;
    		}
            try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(file))) {
                //执行反序列化读取
            	TimerDto[] obj = (TimerDto[])out.readObject();
                //将数组转换成List
                List<TimerDto> plans = Arrays.asList(obj);
                if (plans != null) {
                	for (TimerDto plan : plans) {
                		excuteScheduler(plan);
                	}
                }
            } 
 
		} catch (SchedulerException | IOException | ClassNotFoundException e) {
			logger.error(SCHEDULER_MESSAGE[6], e);
		} 
    }

 
}
