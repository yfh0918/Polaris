package com.polaris.ndi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polaris.core.pojo.Result;
import com.polaris.core.util.ResultUtil;
import com.polaris.ndi.service.LifeCycleServiceFactory;

@RestController
@SuppressWarnings("rawtypes")
public class VedioGrabberController {
    private static Logger logger = LoggerFactory.getLogger(VedioGrabberController.class);
    @PostMapping("/vedio/start")
    public Result start(
            @RequestParam(required = false, value = "streamLocation") String streamLocation,
            @RequestParam(required = false, value = "streamType",defaultValue = "image") String streamType) {
        try {
            LifeCycleServiceFactory.get()
                                   .setType(streamType)
                                   .start(streamLocation);
            return ResultUtil.success();
        } catch (Exception e) {
            logger.error("ERROR:{}",e);
            return ResultUtil.fail(e.getMessage());
        }
        
    }
    
    @PostMapping("/vedio/stop")
    public Result stop() {
        LifeCycleServiceFactory.get().stop();
        return ResultUtil.success();
    }
    
    @GetMapping("/vedio/status")
    public Result status() {
        return ResultUtil.success(LifeCycleServiceFactory.get().isRunning());
    }
}
