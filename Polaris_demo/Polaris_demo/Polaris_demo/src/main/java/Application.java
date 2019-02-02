

import com.polaris.comm.config.ConfClient;
import com.polaris.dubbo.supports.MainSupport;

public class Application {
	
	public static void main(String[] args) throws Exception { 
		
		//设置引用名称
		ConfClient.setAppName("Polaris_demo");
		
		//启动dubbo服务
		MainSupport.startDubboServer(args);
    } 
	

}
