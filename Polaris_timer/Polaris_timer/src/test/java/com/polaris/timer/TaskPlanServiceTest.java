package com.polaris.timer;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.polaris.comm.util.SpringUtil;
import com.polaris.timer.api.dto.TimerDto;
import com.polaris.timer.api.service.TimerService;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TaskPlanServiceTest extends TestCase {
	TimerService timerService;
	
	public void setUp() throws Exception {
		timerService = SpringUtil.getBean(TimerService.class);
	}

	public TaskPlanServiceTest() {
	}

	public static Test suite() {
		return new TestSuite(TaskPlanServiceTest.class);
	}

	/**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	findActivePlans_001();
    }
    
    public void findActivePlans_001() {
    	@SuppressWarnings("resource")
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    	timerService = applicationContext.getBean(TimerService.class);
    	TimerDto dto = new TimerDto();
    	dto.setName("1");

    	List<TimerDto> lists = timerService.findActivePlans(dto);
		System.out.println(lists);
		Assert.assertNotNull(dto.getDatas());
	}
	    
}
