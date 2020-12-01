package com.polaris.container.servlet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * 封装返回信息
     *
     * @return
     */
    public static void returnJson(HttpServletResponse response, String json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            logger.error("returnJson异常", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public static void download(HttpServletResponse response, File... files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }
        OutputStream out = response.getOutputStream();
        try (WritableByteChannel outChannel = Channels.newChannel(out)){
            for (File file : files) {
                download0(outChannel, file.getAbsolutePath());
            }
        } 
    }
    
    public static void download(HttpServletResponse response, String... filePaths) throws IOException {
        if (filePaths == null || filePaths.length == 0) {
            return;
        }
        OutputStream out = response.getOutputStream();
        try (WritableByteChannel outChannel = Channels.newChannel(out)){
            for (String filePath : filePaths) {
                download0(outChannel, filePath);
            }
        } 
    }
    
    private static void download0(WritableByteChannel outChannel, String filePath) throws IOException {
        try (FileInputStream inputstream = new FileInputStream(filePath);
             FileChannel inChannel= inputstream.getChannel()) {
               inChannel.transferTo(0, inChannel.size(), outChannel);
        }  
    }
}
