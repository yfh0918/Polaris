

import com.polaris.comm.config.ConfClient;
import com.polaris.gateway.support.ApplicationSupport;

public class Application {
	
    public static void main(String[] args) {
    	
    	//载入spring
    	ConfClient.setAppName("mwclg-gateway");

    	//启动网关应用
    	ApplicationSupport.startGateway();
    }
}
