package com.polaris.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
//	private com.polaris.core.schedule.CronScheduledThreadPoolExecutor cronScheduledCronThreadPoolExecutor = new com.polaris.core.schedule.CronScheduledThreadPoolExecutor(1, new ThreadFactory() {
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread thread = new Thread(r);
//            thread.setName("com.banyan.customer.schedule.AccountCheckSchedule");
//            return thread;
//        }
//    });
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
//        String cron = ConfClient.get("account.schedule.cron", "0 0 10 * * ?");
//        cronScheduledCronThreadPoolExecutor.scheduleWithCron(
//                new BusinessTask(),
//                cron, CronType.QUARTZ);
    }
}
