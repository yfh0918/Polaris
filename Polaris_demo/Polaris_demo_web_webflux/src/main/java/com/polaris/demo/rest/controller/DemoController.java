package com.polaris.demo.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class DemoController {

    @GetMapping(value = "/test")
    public Mono<String> test() {
        return Mono.just("test");
    }
}
