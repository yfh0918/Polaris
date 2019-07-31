

import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
public class Application
{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args);
    }
}
