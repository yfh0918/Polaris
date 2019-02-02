package com.polaris.demo.core.entry;

import org.springframework.stereotype.Service;

import com.polaris.comm.adapter.ServiceAdapter;
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

}
