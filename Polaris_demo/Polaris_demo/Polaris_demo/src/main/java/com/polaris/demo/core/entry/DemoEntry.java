package com.polaris.demo.core.entry;

import org.apache.dubbo.config.annotation.Service;

import com.polaris.core.adapter.ServiceAdapter;
import com.polaris.core.util.EncryptUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.demo.api.dto.DemoDto;
import com.polaris.demo.api.service.DemoEntryIF;
import com.polaris.demo.core.service.TestService;

@Service
public class DemoEntry implements DemoEntryIF {

    /**
     * 测试
     *
     * @return
     */
    @Override
    public DemoDto test(DemoDto dto) {
        //定义返回对象
        TestService testService = ServiceAdapter.findServiceImpl(TestService.class);
        System.out.println("this is server0");
        return testService.test(dto);

    }
    
    /**
     * 测试
     *
     * @return
     */
    @Override
    public DemoDto test2(DemoDto dto) {
        //定义返回对象
        TestService testService = ServiceAdapter.findServiceImpl(TestService.class);
        return testService.test(dto);

    }
    
	/**
	 * Main主函数输出加密结果
	 * 
	 * @param args
	 * @return
	 */
	public static void main(String[] args) throws Exception { 
		String value = System.getProperty("value");
		if (StringUtil.isEmpty(value)) {
			System.out.println("请输入需要加密的字符串 -Dvalue=xxxxxxx");
			return;
		}
		EncryptUtil en = EncryptUtil.getInstance();
		String result = en.encrypt(EncryptUtil.START_WITH, value);
		System.out.println(result);
    }

}
