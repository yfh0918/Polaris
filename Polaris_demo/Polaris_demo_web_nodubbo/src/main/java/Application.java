

import com.polaris.demo.configurer.RootConig;
import com.polaris.demo.configurer.WebConfig;
import com.polaris.http.initializer.WebConfigInitializer;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
public class Application
{
    
    public static void main( String[] args ) throws Exception
    {
    	
//    	//载入需要的参数，过滤器，监听器等
//    	WebConfigInitializer.loadRootConfig(RootConig.class);
//    	WebConfigInitializer.loadWebConfig(WebConfig.class);
//    	WebConfigInitializer.loadInitParameters(key, value);
//    	WebConfigInitializer.loadFilter(filterName, filter, urlPatterns);
//    	WebConfigInitializer.loadListener(listener);
//    	MainSupport.startWebServer(args);//resteasy
    	
		//启动WEB
    	MainSupport.startWebServer(args, new Class[]{RootConig.class}, new Class[]{WebConfig.class});//springmvc
    }
}
