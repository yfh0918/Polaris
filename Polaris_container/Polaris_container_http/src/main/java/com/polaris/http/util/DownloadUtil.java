package com.polaris.http.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polaris.http.Constant;
import com.polaris.comm.util.LogUtil;

public class DownloadUtil {
    private static final LogUtil logger = LogUtil.getInstance(DownloadUtil.class);
    /**
     * 用户浏览器关键字：IE
     */
    private static final String USER_AGENT_IE = "MSIE";


    /**
     * 根据不同的浏览器设置下载文件名称的编码
     *
     * @param request
     * @param fileName
     * @return 文件名称
     */
    public static String encodeDownloadFileName(HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.indexOf(USER_AGENT_IE) > 0) {// 用户在用IE
            try {
                return URLEncoder.encode(fileName, Constant.UTF_CODE);
            } catch (UnsupportedEncodingException ignore) {
            }
        } else {
            try {
                return new String(fileName.getBytes(Constant.UTF_CODE), "ISO-8859-1");
            } catch (UnsupportedEncodingException ignore) {
            }
        }
        return fileName;
    }

    /**
     * 下载 pdf
     *
     * @param fileName 下载的文件名
     * @param tempPath pdf临时文件 *.pdf
     */
    public static void downloadPdf(HttpServletRequest request, HttpServletResponse response, String fileName, String tempPath) {
        fileName = encodeDownloadFileName(request, fileName);
        response.setContentType("text/html;charset=utf-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        logger.info("INFO:[fileName:{}]", fileName);
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        File file = new File(tempPath);
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } catch (IOException e) {
            logger.info("ERROR:" + e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                boolean deleted = file.delete();
                logger.info("INFO:downloadPdf; delete temp pdf file:{}，地址:{}", deleted, file.getAbsolutePath());
            } catch (IOException e) {
                logger.info("ERROR:" + e);
            }
        }
    }

    /**
     * 下载服务器文件
     *
     * @param tempPath 文件路径
     */
    public static void downloadFile(HttpServletResponse response, String tempPath) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        File file = new File(tempPath);
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } catch (IOException e) {
            logger.info("ERROR:" + e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                logger.info("ERROR:" + e);
            }
        }
    }
}
