package com.polaris.ndi.service;

import com.polaris.ndi.pojo.EnumPlatform;
import com.polaris.ndi.pojo.OSInfo;
import com.sun.jna.Native;

public class CAPIFactory {
    public static CAPIcommon get() {
        if (OSInfo.getOSname() == EnumPlatform.Windows) {
            return (CAPIcommon)Native.load("config/CAPIcommon.dll",CAPIcommon.class);
        } else {
            return (CAPIcommon)Native.load("config/libCAPIcommon.so",CAPIcommon.class);
        }
    }
}
