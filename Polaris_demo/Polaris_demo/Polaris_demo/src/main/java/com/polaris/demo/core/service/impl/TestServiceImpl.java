package com.polaris.demo.core.service.impl;

import org.springframework.stereotype.Service;

import com.polaris.demo.api.dto.DemoDto;
import com.polaris.demo.core.service.TestService;

@Service
public class TestServiceImpl implements TestService {

    @Override
    public DemoDto test(DemoDto dto) {
    	System.out.println("this is server1");
        return dto;
    }
}
