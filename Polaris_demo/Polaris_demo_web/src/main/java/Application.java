

import com.polaris.comm.config.ConfClient;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
public class Application
{
    
    public static void main( String[] args ) throws Exception
    {

		//设置引用名称
		ConfClient.setAppName("Polaris_demo_web");

		//启动WEB
    	MainSupport.startWebServer(args);
    }
}
