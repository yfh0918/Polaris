package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * @Author: lei.chen@hcit.ai
 * @Description:
 * @CreateTiem: 2019/12/4 17:43
 **/
@RestController
@RequestMapping("/user")
public class DemoController {


    /**
     * 公众号用户登记
     *
     * @param request
     * @param phoneToken 手机号及验证码
     * @param code       用户输入验证码
     * @return
     */
    @GetMapping(value = "/test")
    public Mono<String> test(HttpServletRequest request) {
        return Mono.just("test");
    }
}
