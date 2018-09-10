package com.polaris.http.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.polaris.comm.util.FileUtil;
import com.polaris.comm.util.LogUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

public class UploadUtil {
    private static final LogUtil logger = LogUtil.getInstance(UploadUtil.class);

    private UploadUtil() {
    }

    /**
     * 随机生成新文件名
     *
     * @return 新文件名
     */
    public static String getNewFileName(String fileName) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        if (StringUtils.isEmpty(fileName) || !fileName.contains(".")) {
            return uuid;
        }
        return uuid + fileName.substring(fileName.lastIndexOf('.'), fileName.length());
    }

    /**
     * 上传文件 到服务器
     *
     * @param input    文件参数
     * @param filepath 传到本地的文件路径
     * @param fileName 上传到本地的文件名称
     */
    public static String uploadFile(MultipartFormDataInput input, String filepath, String fileName) {
        //提出文件
        List<InputPart> inputParts = input.getFormDataMap().get("file");
        String tpFilePath = null;
        //判断临时目录存不存在，不存在则创建
        FileUtil.createDirectory(filepath);
        if (CollectionUtil.isNotEmpty(inputParts)) {
            //这里上传只有一个文件
            InputPart inputPart = inputParts.get(0);
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                tpFilePath = filepath + fileName;
                writeFile(bytes, tpFilePath);
            } catch (IOException e) {
                logger.error("ERROR：", e);
                logger.error(StrUtil.format("获取文件出错，错误信息:{}", e.getMessage()));
            }
        }
        return tpFilePath;
    }

    /**
     * 创建文件，并写入文件内容
     */
    private static void writeFile(byte[] content, String filename) throws IOException {
        File file = FileUtil.makeFile(filename);
        FileOutputStream fop = null;
        try {
            fop = new FileOutputStream(file);
            fop.write(content);
            fop.flush();
        } catch (FileNotFoundException e) {
            logger.error("ERROR", e);
        } finally {
            FileUtil.closeFileOutputStream(fop);
        }
    }
}
