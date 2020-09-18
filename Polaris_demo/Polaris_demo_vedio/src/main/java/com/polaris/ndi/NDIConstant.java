package com.polaris.ndi;

public abstract class NDIConstant {
    
    //0:initial 1:starting 2:started 3:stopping 4:stopped
    public static final int INITIAL = 0;
    public static final int STARTING = 1;
    public static final int STARTED = 2;
    public static final int STOPPING = 3;
    public static final int STOPPED = 4;
    
    //define constant
    public static final String SPLIT_CHAR = "-banyanNDI-";
    public static final String HOST_NAME_KEY = "banyan.ndi.hostname";
    public static final String HOST_NAME_DEFAULT_WINDOWS_VALUR = "COM3";
    public static final String HOST_NAME_DEFAULT_LINUX_VALUE = "/dev/ttyUSB0";
    public static final String HOST_NAME_DEFAULT_MAC_VALUE = "/dev/cu.usbserial-001014FA";
    public static final String SCU_HOST_NAME_KEY = "banyan.ndi.scu.hostname";
    public static final String SCU_HOST_NAME_DEFAULT_VALUE = "";

}
