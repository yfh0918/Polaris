package com.polaris.container.servlet.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class ResponseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * 封装返回信息
     *
     * @return
     */
    public static void returnJson(HttpServletResponse response, JSONObject json) {
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

}
