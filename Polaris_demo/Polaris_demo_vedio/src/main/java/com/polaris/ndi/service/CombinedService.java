package com.polaris.ndi.service;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.polaris.core.thread.ThreadPoolBuilder;
import com.polaris.core.util.StringUtil;
import com.polaris.ndi.NDIConstant;
import com.polaris.ndi.util.MemoryUtil;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import cn.hutool.core.thread.ThreadUtil;

@Service
public class CombinedService {
    private static Logger logger = LoggerFactory.getLogger(CombinedService.class);

    //thread pool
    private static ThreadPoolExecutor threadPool = ThreadPoolBuilder.newBuilder()
            .poolName("NDIHandler Thread Pool")
            .coreThreads(2)
            .maximumThreads(2)
            .keepAliveSeconds(10l)
            .workQueue(new LinkedBlockingDeque<Runnable>(10000))
            .build();
    private BlockingQueue<String> tracingDataQueue = new ArrayBlockingQueue<>(10);
    
    //等待时间
    private volatile int WAIT_TIME = 100;
    
    //CAPI
    private CAPIcommon commonCAPI = CAPIFactory.get();
    private Pointer capi = commonCAPI.CombinedNDIApi();
    
    //0:initial 1:starting 2:started 3:stopping 4:stopped
    private volatile int STATUS = 0;
    
    //websocket-session
    private Set<Session> sessions = new HashSet<>();
    public CombinedService() {
        init();
    }
    private void init() {
      Thread thread = ThreadUtil.newThread(new Runnable() {
          @Override
          public void run() {
              while (true) {
                  try {
                      String result = tracingDataQueue.take();
                      String[] trackingDatas = result.split(NDIConstant.SPLIT_CHAR);
                      for (String trackingData : trackingDatas) {
                          if (StringUtil.isNotEmpty(trackingData)) {
                              output(trackingData,false);
                          }
                      }
                  } catch (Exception e) {
                      logger.error("error:{}", e);
                  }
              }
          }
      }, "getTracingData thread", true);
      thread.start();
    }
    
    /**
     * 启动NDI
     */
    public int start(String hostname, String scu_hostname){ 
        
        //预分配内存
        Memory hostMemory = MemoryUtil.of(hostname);
        Memory versionMemory = MemoryUtil.of("Features.Firmware.Version");
        
        try {
            // conditional judgment
            if (getStatus(true) == NDIConstant.STARTING || getStatus(false) == NDIConstant.STARTED || getStatus(false) == NDIConstant.STOPPING) {
                return getStatus(false);
            }
            
            // update status
            setStatus(NDIConstant.STARTING);
            
            // capi instance
            if (capi == null) {
                capi = commonCAPI.CombinedNDIApi();
            }

            // Attempt to connect to the device
            int connectResult = commonCAPI.connectNDI(capi,hostMemory);
            if (connectResult != 0) {
                output("Failed to connect to the "+hostname);
                setStatus(NDIConstant.STOPPED);
                return getStatus(false);
            }
            
            // Print the firmware version for debugging purposes
            output("Successfully connected to the "+hostname);
            Memory resultData = MemoryUtil.of(100);
            commonCAPI.getUserParameter(capi,versionMemory,resultData);
            output(MemoryUtil.getString(resultData));

            // Determine if the connected device supports the BX2 command
            boolean apiSupportsBX2 = commonCAPI.determineApiSupportForBX2(capi);

            // Initialize the system. This clears all previously loaded tools, unsaved settings etc...
            int errorCode = commonCAPI.initialize(capi);
            if (errorCode < 0) {
                resultData = MemoryUtil.of(100);
                commonCAPI.errorToString(capi, errorCode,resultData);
                output("capi.initialize() failed: " + MemoryUtil.getString(resultData));
                setStatus(NDIConstant.STOPPED);
                return getStatus(false);
            }
            
            /*
            // Demonstrate error handling by asking for tracking data in the wrong mode
            resultData.clear();
            commonCAPI.getTrackingDataTX(capi,resultData);
            output(MemoryUtil.getString(resultData));

            // Demonstrate getting/setting user parameters
            resultData.clear();
            commonCAPI.getUserParameter(capi,MemoryUtil.of("Param.User.String0"), resultData);
            output(MemoryUtil.getString(resultData));
            errorCode = commonCAPI.setUserParameter(capi,MemoryUtil.of("Param.User.String0"),MemoryUtil.of("customString"));
            if (errorCode < 0) {
                resultData.clear();
                commonCAPI.errorToString(capi, errorCode,resultData);
                output("capi.setUserParameter(Param.User.String0, customString)" + MemoryUtil.getString(resultData));
            }
            resultData.clear();
            commonCAPI.getUserParameter(capi,MemoryUtil.of("Param.User.String0"),resultData);
            output(MemoryUtil.getString(resultData));
            errorCode = commonCAPI.setUserParameter(capi,MemoryUtil.of("Param.User.String0"),MemoryUtil.of(""));
            if (errorCode < 0) {
                resultData.clear();
                commonCAPI.errorToString(capi, errorCode, resultData);
                output("capi.setUserParameter(Param.User.String0, emptyString)" + MemoryUtil.getString(resultData));
            }

            // Various tool types are configured in slightly different ways
            output("Configuring Passive Tools - Loading .rom Files...");
            commonCAPI.loadTool(capi,MemoryUtil.of("C:/projects/jdk8_64Bit/bin/sroms/8700338.rom"));
            commonCAPI.loadTool(capi,MemoryUtil.of("C:/projects/jdk8_64Bit/bin/sroms/8700340.rom"));
            output("Configuring an Active Wireless Tool - Loading .rom File...");
            commonCAPI.loadTool(capi,MemoryUtil.of("C:/projects/jdk8_64Bit/bin/sroms/active-wireless.rom"));
            if (scu_hostname.length() > 0) {
                commonCAPI.configureActiveTools(capi,MemoryUtil.of(scu_hostname));
            }
            */

            // Once loaded or detected, tools are initialized and enabled the same way
            commonCAPI.initializeAndEnableTools(capi);

            // Once the system is put into tracking mode, data is returned for whatever tools are enabled
            output("Entering tracking mode and collecting data...");
            
            errorCode = commonCAPI.startTracking(capi);
            if (errorCode < 0) {
                resultData = MemoryUtil.of(100);
                commonCAPI.errorToString(capi, errorCode, resultData);
                output("capi.startTracking()" + MemoryUtil.getString(resultData));
                setStatus(NDIConstant.STOPPED);
                return getStatus(false);
            }
            
            //update status
            setStatus(NDIConstant.STARTED);
            
            //new thread run
            threadPool.execute(
                    new Runnable() {
                      @Override
                      public void run() {
                          output("[alerts] [buttons] Frame#,ToolHandle,Face#,TransformStatus,Q0,Qx,Qy,Qz,Tx,Ty,Tz,Error,#Markers,State,Tx,Ty,Tz");
                          while(getStatus(false)==NDIConstant.STARTED) {
                              
                              //get result
                              Memory trackingData = MemoryUtil.of(100);
                              commonCAPI.getTracingData(capi, apiSupportsBX2,trackingData);
                              String result = MemoryUtil.getString(trackingData);

                              //not empty and changed data will be output
                              if (StringUtil.isNotEmpty(result)) {
                                  try {
                                      tracingDataQueue.put(result);
                                } catch (InterruptedException e) {}
                              }
                              try {
                                  Thread.sleep(WAIT_TIME);
                              } catch (Exception ex) {}
                          } 
                      }
                    }
             );
            
            return getStatus(false);
            
        } finally {
            //释放内存
            MemoryUtil.clear(hostMemory);
            MemoryUtil.clear(versionMemory);
        }
    } 
    
    /**
     * 数据输出
     */
    private void output(String value) {
        output(value,true);
    }
    private void output(String value,boolean loggerOut) {
        if (loggerOut) {
            logger.info(value);
        }
        for (Session session : sessions) {
            if (session != null && session.isOpen()) { 
                try {
                    session.getBasicRemote().sendText(value);
                } catch (IOException e) {
                    logger.error("error:",e);
                }
            }
        }
        
    }
    
    /**
     * 停止NDI
     */
    public int stop() {
        
        //conditional judgment
        if (getStatus(true) != NDIConstant.STARTING && getStatus(false) != NDIConstant.STARTED) {
            output("cannot stop if not in starting or stated");
            return getStatus(false);
        }
        
        //update status
        setStatus(NDIConstant.STOPPING);
        
        //waiting 0.5 seconds
        try {
            Thread.sleep(WAIT_TIME*2+100);
        } catch (InterruptedException e) {
            //ignore
        }
        
        //Stop tracking (back to configuration mode)
        output("Leaving tracking mode and returning to configuration mode...");
        int errorCode = commonCAPI.stopTracking(capi);
        if (errorCode < 0) {
            Memory resultData = MemoryUtil.of(100);
            commonCAPI.errorToString(capi, errorCode,resultData);
            output("capi.stopTracking()" + MemoryUtil.getString(resultData));
        }
        
        //update status
        setStatus(NDIConstant.STOPPED);
        
        //return status
        return getStatus(false);
    }
    
    private void setStatus(int status) {
        STATUS = status;
        if (STATUS == NDIConstant.INITIAL) {
            output("NDI is initial");
        } else if (STATUS == NDIConstant.STARTING) {
            output("NDI is starting");
        } else if (STATUS == NDIConstant.STARTED) {
            output("NDI is started");
        } else if (STATUS == NDIConstant.STOPPING) {
            output("NDI is stopping");
        } else if (STATUS == NDIConstant.STOPPED) {
            output("NDI is stopped");
        }
    }
    public int getStatus(boolean out) {
        if (out) {
            if (STATUS == NDIConstant.INITIAL) {
                output("NDI is initial");
            } else if (STATUS == NDIConstant.STARTING) {
                output("NDI is starting");
            } else if (STATUS == NDIConstant.STARTED) {
                output("NDI is started");
            } else if (STATUS == NDIConstant.STOPPING) {
                output("NDI is stopping");
            } else if (STATUS == NDIConstant.STOPPED) {
                output("NDI is stopped");
            } 
        }
        return STATUS;
    }
    
    public void addSession(Session session) {
        sessions.add(session);
    }
    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
    public String setUserParameter(String key, String value) {
        int errorCode = commonCAPI.setUserParameter(capi,MemoryUtil.of(key),MemoryUtil.of(value));
        String result = "";
        if (errorCode < 0) {
            Memory resultData = MemoryUtil.of(100);
            commonCAPI.errorToString(capi, errorCode, resultData);
            result = "capi.setUserParameter(Param.User.String0, customString)" + MemoryUtil.getString(resultData);
            output(result);
        } else {
            result = "setUserParameter is ok"+" returnCode:"+errorCode;
            output(result);
        }
        return result;
    }
    
    public String getUserParameter(String key) {
        Memory resultData = MemoryUtil.of(100);
        commonCAPI.getUserParameter(capi,MemoryUtil.of(key),resultData);
        String value = MemoryUtil.getString(resultData);
        output(value);
        return value;
    }
    
    public void setWaitTime(int waitTime) {
        WAIT_TIME = waitTime;
    }
    
    public static void main(String[] args) throws InterruptedException{ 
        //thread pool
        ThreadPoolExecutor threadPool = ThreadPoolBuilder.newBuilder()
                .poolName("LogPickController Thread Pool")
                .coreThreads(2)
                .maximumThreads(2)
                .keepAliveSeconds(10l)
                .workQueue(new LinkedBlockingDeque<Runnable>(10000))
                .build();

        //service
        CombinedService service = new CombinedService();
        service.start(NDIConstant.HOST_NAME_DEFAULT_WINDOWS_VALUR,NDIConstant.SCU_HOST_NAME_DEFAULT_VALUE);
        threadPool.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
                service.stop();
            }
        });
        
    }

}
