
import com.polaris.comm.config.ConfClient;
import com.polaris.dubbo.supports.MainSupport;

public class Application {
	public static void main(String[] args) throws Exception { 
		ConfClient.setAppName("Polaris_log");
		MainSupport.startDubboServer(args);
    } 
}
