package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping(value="users")
public class FeignController {

    @RequestMapping(value="/list",method={RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})    
    public User list(@RequestBody User user) throws InterruptedException{
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
        user.setId(new Long(request.getLocalPort()));
        user.setUsername(user.getUsername().toUpperCase());
        System.out.println(user.getId() + "," + user.getUsername());
        return user;
    }

}
