package com.polaris.ndi.util;

import com.sun.jna.Memory;
import com.sun.jna.Native;

public class MemoryUtil {

    public static Memory of(String value) {
        return of(value, Native.WCHAR_SIZE * (value.length() + 1));
    }
    public static Memory of(String value, long size) {
        Memory mem = new Memory(size);
        mem.setString(0, value);
        return mem; 
    }
    public static Memory of(long size) {
        Memory mem = new Memory(size);
        return mem; 
    }
    public static String getString(Memory memory) {
        if (memory == null) {
            return "";
        }
        String result = memory.getString(0);
        clear(memory);
        if (result == null) {
            return "";
        }
        return result.trim();
    }
    public static void clear(Memory memory) {
        long peer = Memory.nativeValue(memory);
        Native.free(peer);//手动释放内存
        Memory.nativeValue(memory, 0);//避免Memory对象被GC时重复执行Nativ.free()方法
        memory = null;
    }
}
