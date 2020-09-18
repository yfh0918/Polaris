package com.polaris.ndi.service;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface CAPIcommon extends Library {
        
    //获取api句柄对象
    Pointer CombinedNDIApi();
    
    //获取版本信息
    void getVersion(Pointer capi,Pointer resultData);
    
    //连接NDI
    int connectNDI(Pointer capi, Pointer hostname);
    
    //获取参数信息
    void getUserParameter(Pointer capi, Pointer paramName,Pointer resultData);
    
    //设置参数信息
    int setUserParameter(Pointer capi, Pointer paramName, Pointer value);
    
    //初始化处理
    int initialize(Pointer capi);
    
    //检测是否是BX2模式
    boolean determineApiSupportForBX2(Pointer capi);
    
    //错误信息
    void errorToString(Pointer capi,int errorCode,Pointer resultData);
    
    //发送TX命令
    void getTrackingDataTX(Pointer capi,Pointer resultData);
    
    //loading tools
    void loadTool(Pointer capi,Pointer romFile);
    
    //Demonstrate detecting active tools.
    void configureActiveTools(Pointer capi, Pointer scuHostname);
    
    // Once loaded or detected, tools are initialized and enabled the same way
    void initializeAndEnableTools(Pointer capi);
    
    //start tracking
    int startTracking(Pointer capi);

    //get tracing Data
    void getTracingData(Pointer capi,boolean apiSupportsBX2,Pointer resultData);
    
    //stop Tracking
    int stopTracking(Pointer capi);
}
