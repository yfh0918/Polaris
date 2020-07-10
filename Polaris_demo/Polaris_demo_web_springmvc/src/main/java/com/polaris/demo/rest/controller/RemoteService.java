package com.polaris.demo.rest.controller;

import feign.Headers;
import feign.RequestLine;

public interface RemoteService {    
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("GET /users/list")
    User getOwner(User user);
}

