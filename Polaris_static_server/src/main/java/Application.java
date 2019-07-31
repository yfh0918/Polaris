

import com.mwclg.gateway.support.ApplicationSupport;
import com.polaris.core.config.ConfClient;

public class Application {
    public static void main(String[] args) {
    	
    	//载入spring
    	ConfClient.setAppName("Polaris_static_server");

    	//启动网关应用
    	ApplicationSupport.startGateway();
    }
}
