package com.polaris.http.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.polaris.comm.util.FileUtil;
import com.polaris.comm.util.LogUtil;
import com.polaris.http.dto.Ftp;

/**
 * @标题: FtpUtil.java
 * @包名: com.polaris.utils
 * @描述:
 * @作者: yanghao
 * @时间: Dec 21, 2015 10:40:44 PM
 * @版权: (c) 2015, 卫宁软件科技有限公司
 */
public class FtpUtil {
    private static LogUtil logger = LogUtil.getInstance(FtpUtil.class);

    private static FTPClient ftp;

    private FtpUtil() {
    }

    /**
     * 连接ftp
     */
    public static boolean connectFtp(Ftp f) throws Exception {
        ftp = new FTPClient();
        int reply;
        if (f.getPort() == null) {
            ftp.connect(f.getIpAddr(), 21);
        } else {
            ftp.connect(f.getIpAddr(), f.getPort());
        }
        ftp.login(f.getUserName(), f.getPwd());
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        if (f.getPath() != null) {
            if (!ftp.changeWorkingDirectory(f.getPath())){
                ftp.makeDirectory(f.getPath());
                ftp.changeWorkingDirectory(f.getPath());
            }
        }
        return true;
    }

    /**
     * 切换目录 没有则创建
     * @param dir 目录
     */
    public static boolean changeDir(String dir) throws IOException {
        return StringUtils.isNotBlank(dir) && (ftp.changeWorkingDirectory(dir) || ftp.makeDirectory(dir));
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp() {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException e) {
                logger.error("closeFtp.IOException", e);
            }
        }
    }

    /**
     * ftp上传文件
     */
    public static void upload(File f) throws Exception {
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());
            String[] files = f.list();
            for (String fstr : files) {
                File file1 = new File(f.getPath() + File.separator + fstr);
                if (file1.isDirectory()) {
                    upload(file1);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + File.separator + fstr);
                    FileInputStream input = new FileInputStream(file2);
                    boolean b = ftp.storeFile(file2.getName(), input);
                    logger.info("上传文件到FTP:>>>  文件名:" + file2.getName() + ",>> 上传结果:" + b);
                    input.close();
                }
            }
        } else {
            File file2 = new File(f.getPath());
            FileInputStream input = new FileInputStream(file2);
            boolean b = ftp.storeFile(file2.getName(), input);
            logger.info("上传文件到FTP:>>>  文件名:" + file2.getName() + ",>> 上传结果:" + b);
            input.close();
        }
    }

    /**
     * 下载链接配置
     *
     * @param f
     * @param localBaseDir 本地目录
     * @throws Exception
     */
    public static void startDown(Ftp f, String localBaseDir, String fileName) throws Exception {
        if (FtpUtil.connectFtp(f)) {
            try {
                FTPFile[] files = null;
                ftp.setControlEncoding("GBK");
                ftp.enterLocalPassiveMode();
                files = ftp.listFiles();
                for (int i = 0; i < files.length; i++) {
                    try {
                        if (fileName.equals(files[i].getName())) {
                            downloadFile(files[i], localBaseDir);
                            break;
                        }
                    } catch (Exception e) {
                        logger.error(e);
                        logger.error("<" + files[i].getName() + ">下载失败");
                    }
                }
            } catch (Exception e) {
                logger.error(e);
                logger.error("下载过程中出现异常");
            }
        } else {
            logger.error("链接失败！");
        }

    }

    /**
     * 下载FTP文件 当你需要下载FTP文件的时候，调用此方法 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
     */
    private static void downloadFile(FTPFile ftpFile, String relativeLocalPath) {
        if (ftpFile.isFile()) {
            if (ftpFile.getName().indexOf("?") == -1) {
                try {
                    FileUtil.createDirectory(relativeLocalPath);
                    File locaFile = new File(relativeLocalPath + File.separator
                            + ftpFile.getName());
                    // 如果文件存在就删除后下载
                    if (locaFile.exists()) {
                        if (!locaFile.delete()) {
                            logger.error("删除文件失败！");
                        }
                    }
                    try (OutputStream outputStream = new FileOutputStream(locaFile);) {
                        boolean b = ftp.retrieveFile(ftpFile.getName(), outputStream);
                        logger.info("文件下载:文件名:" + ftpFile.getName() + ">>  下载结果:" + b);
                        outputStream.flush();
                    }
                } catch (Exception e) {
                    logger.error(e);
                }

            }
        } else {
            String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
            String newRemote = new String(ftpFile.getName().toString());
            File fl = new File(newlocalRelatePath);
            if (!fl.exists()) {
                fl.mkdirs();
            }
            try {
                newlocalRelatePath = newlocalRelatePath + File.separator;
                newRemote = newRemote + File.separator;
                String currentWorkDir = ftpFile.getName().toString();
                boolean changedir = ftp.changeWorkingDirectory(currentWorkDir);
                if (changedir) {
                    FTPFile[] files = null;
                    files = ftp.listFiles();
                    ftp.enterLocalPassiveMode();
                    for (int i = 0; i < files.length; i++) {
                        downloadFile(files[i], newlocalRelatePath);
                    }
                }
                if (changedir) {
                    ftp.changeToParentDirectory();
                }
            } catch (Exception e) {
                logger.error("downloadFile.Exception", e);
            }
        }
    }
}

