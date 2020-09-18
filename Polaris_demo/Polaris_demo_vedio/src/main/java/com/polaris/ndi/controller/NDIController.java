package com.polaris.ndi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polaris.core.config.ConfClient;
import com.polaris.core.pojo.Result;
import com.polaris.core.util.ResultUtil;
import com.polaris.ndi.NDIConstant;
import com.polaris.ndi.pojo.EnumPlatform;
import com.polaris.ndi.pojo.OSInfo;
import com.polaris.ndi.service.CombinedService;

@RestController
@SuppressWarnings("rawtypes")
public class NDIController {
    
    @Autowired
    private CombinedService combinedService;

    @PostMapping("/ndi/start")
    public Result start() {
        String hostname = "";
        if (OSInfo.getOSname() == EnumPlatform.Windows) {
            hostname = ConfClient.get(NDIConstant.HOST_NAME_KEY, NDIConstant.HOST_NAME_DEFAULT_WINDOWS_VALUR);
        } else if (OSInfo.getOSname() == EnumPlatform.Mac_OS || OSInfo.getOSname() == EnumPlatform.Mac_OS_X){
            hostname = ConfClient.get(NDIConstant.HOST_NAME_KEY, NDIConstant.HOST_NAME_DEFAULT_MAC_VALUE);
        } else {
            hostname = ConfClient.get(NDIConstant.HOST_NAME_KEY, NDIConstant.HOST_NAME_DEFAULT_LINUX_VALUE);
        }
        String scuHostname = ConfClient.get(NDIConstant.SCU_HOST_NAME_KEY, NDIConstant.SCU_HOST_NAME_DEFAULT_VALUE);
        return ResultUtil.success(combinedService.start(hostname,scuHostname));
    }
    
    @PostMapping("/ndi/stop")
    public Result stop() {
        return ResultUtil.success(combinedService.stop());
    }
    
    @GetMapping("/ndi/status")
    public Result status() {
        return ResultUtil.success(combinedService.getStatus(false));
    }
    
    @PostMapping("/ndi/waitTime")
    public Result waitTime(@RequestParam(value = "waitTime", defaultValue = "100") Integer waitTime) {
        if (waitTime <= 0) {
            return ResultUtil.fail("waitTime 必须比0大");
        }
        combinedService.setWaitTime(waitTime);
        return ResultUtil.success();
    }
    
    @GetMapping("/ndi/setUserParameter")
    public Result setUserParameter(@RequestParam("key") String key,@RequestParam("value") String value) {
        return ResultUtil.success(combinedService.setUserParameter(key, value));
    }
    
    @GetMapping("/ndi/getUserParameter")
    public Result getUserParameter(@RequestParam("key") String key) {
        return ResultUtil.success(combinedService.getUserParameter(key));
    }
}
