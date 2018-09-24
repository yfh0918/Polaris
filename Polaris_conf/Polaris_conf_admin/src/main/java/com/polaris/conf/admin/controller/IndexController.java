package com.polaris.conf.admin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.comm.config.ConfClient;
import com.polaris.conf.admin.controller.annotation.PermessionLimit;
import com.polaris.conf.admin.controller.interceptor.PermissionInterceptor;
import com.polaris.conf.admin.core.util.ReturnT;

@Controller
public class IndexController {

    @RequestMapping("/")
    @PermessionLimit(limit=false)
    public String index(Model model, HttpServletRequest request) {
        if (!PermissionInterceptor.ifLogin(request)) {
            return "redirect:/toLogin";
        }
        return "redirect:/namespace";
    }

    @RequestMapping("/toLogin")
    @PermessionLimit(limit=false)
    public String toLogin(Model model, HttpServletRequest request) {
        if (PermissionInterceptor.ifLogin(request)) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value="login", method= RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
        if (!PermissionInterceptor.ifLogin(request)) {
        	
        	try {
                if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)
                        && ConfClient.get("polaris.conf.login.username", false).equals(userName)
                        && ConfClient.get("polaris.conf.login.password", false).equals(password)) {
                    boolean ifRem = false;
                    if (StringUtils.isNotBlank(ifRemember) && "on".equals(ifRemember)) {
                        ifRem = true;
                    }
                    PermissionInterceptor.login(response, ifRem);
                } else {
                    return new ReturnT<String>(500, "账号或密码错误");
                }
    		} catch (Exception ex) {
    			return new ReturnT<String>(500, "账号或密码错误");
    		}
        	

        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping(value="logout", method=RequestMethod.POST)
    @ResponseBody
    @PermessionLimit(limit=false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
        if (PermissionInterceptor.ifLogin(request)) {
            PermissionInterceptor.logout(request, response);
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/help")
    @PermessionLimit
    public String help() {
        return "help";
    }

}
