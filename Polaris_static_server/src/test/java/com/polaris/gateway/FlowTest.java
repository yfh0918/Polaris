package com.polaris.gateway;

import com.mwclg.common.utils.HttpClientUtil;

public class FlowTest implements Runnable {
	public static void main(String[] args) throws InterruptedException {
		int count = 1000;
        for (int i = 0;  i < count; i++) {
        	new Thread(new FlowTest()).start();
        }
    }
	
	@Override
    public void run() { 
  	  System.out.println(HttpClientUtil.doGet("http://localhost:9261/test"));
    }
}
