package com.polaris.demo.rest.controller;

import com.polaris.extension.feign.FeignRequest;

import feign.Headers;
import feign.RequestLine;

@FeignRequest(value="localhost:9045",context="/demospringmvc")
public interface RemoteService {    
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("GET /users/list")
    User getOwner(User user);
}

