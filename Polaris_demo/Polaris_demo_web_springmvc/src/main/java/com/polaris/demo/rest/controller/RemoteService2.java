package com.polaris.demo.rest.controller;

import com.polaris.core.naming.request.NamingRequest;

import feign.Headers;
import feign.RequestLine;

@NamingRequest(value="localhost:9045",context="/demospringmvc")
public interface RemoteService2 {    
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("GET /users/list")
    User getOwner(User user);
}

