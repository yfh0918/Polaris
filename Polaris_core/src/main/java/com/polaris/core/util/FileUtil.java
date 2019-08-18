package com.polaris.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于文件相关操作的工具类，集成了log4j，需要log4j包的支持。
 */
public final class FileUtil {
    private static Logger log4j = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {

    }

    /**
     * 得到文件的输入流，如无法定位文件返回null。
     *
     * @param relativePath 文件相对当前应用程序的类加载器的路径。
     * @return 文件的输入流。
     */
    public static InputStream getResourceStream(String relativePath) {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(relativePath);
    }

    /**
     * 关闭输入流
     *
     * @param is 输入流，可以是null
     */
    public static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log4j.error("closeInputStream.IOException", e);
            }
        }
    }

    public static void closeFileOutputStream(FileOutputStream fos) {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                log4j.error("closeFileOutputStream.IOException", e);
            }
        }
    }

    /**
     * 从文件路径中提取目录路径，如果文件路径不含目录返回null。
     *
     * @param filePath 文件路径。
     * @return 目录路径，不以'/'或操作系统的文件分隔符结尾。
     */
    public static String extractDirPath(String filePath) {
        int separatePos = Math.max(filePath.lastIndexOf('/'), filePath
                .lastIndexOf('\\')); // 分隔目录和文件名的位置
        return separatePos == -1 ? null : filePath.substring(0, separatePos);
    }

    /**
     * 从文件路径中提取文件名, 如果不含分隔符返回null
     *
     * @param filePath
     * @return 文件名, 如果不含分隔符返回null
     */
    public static String extractFileName(String filePath) {
        int separatePos = Math.max(filePath.lastIndexOf('/'), filePath
                .lastIndexOf('\\')); // 分隔目录和文件名的位置
        return separatePos == -1 ? null : filePath.substring(separatePos + 1, filePath.length());
    }

    /**
     * 按路径建立文件，如已有相同路径的文件则不建立。
     *
     * @param filePath 要建立文件的路径。
     * @return 表示此文件的File对象。
     * @throws IOException 如路径是目录或建文件时出错抛异常。
     */
    public static File makeFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.isFile()) {
            return file;
        }
        if (filePath.endsWith("/") || filePath.endsWith("\\")) {
            throw new IOException(filePath + " is a directory");
        }

        String dirPath = extractDirPath(filePath); // 文件所在目录的路径

        if (dirPath != null) { // 如文件所在目录不存在则先建目录
            makeFolder(dirPath);
        }

        if(!file.createNewFile()){
        	log4j.error("删除文件失败！");
        }
        log4j.info("文件已创建: " + filePath);
        return file;
    }

    /**
     * 新建目录,支持建立多级目录
     *
     * @param folderPath 新建目录的路径字符串
     * @return boolean, 如果目录创建成功返回true, 否则返回false
     */
    public static boolean makeFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
                log4j.info("新建目录为：" + folderPath);
            } else {
                log4j.info("新建目录为：" + folderPath);
            }
        } catch (Exception e) {
            log4j.error("新建目录出错: " + folderPath, e);
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param filePathAndName 要删除文件名及路径
     * @return boolean 删除成功返回true,删除失败返回false
     */
    public static boolean deleteFile(String filePathAndName) {
        try {
            File myDelFile = new File(filePathAndName);
            if (myDelFile.exists()) {
                if(!myDelFile.delete()){
                	log4j.error("删除文件失败！");
                }
                log4j.info("文件：" + filePathAndName + " 已删除!!!");
            }
        } catch (Exception e) {
            log4j.error("文件：" + filePathAndName + " 删除出错!!!", e);
            return false;
        }
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 递归删除指定目录中所有文件和子文件夹
     *
     * @param path           某一目录的路径,如"c:\cs"
     * @param ifDeleteFolder boolean值,如果传true,则删除目录下所有文件和文件夹;如果传false,则只删除目录下所有文件,子文件夹将保留
     */
    public static void deleteAllFile(String path, boolean ifDeleteFolder) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        String temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith("\\") || path.endsWith("/")) {
                temp = path + tempList[i];
            } else {
                temp = path + File.separator + tempList[i];
            }
            if ((new File(temp)).isFile()) {
                deleteFile(temp);
            } else if ((new File(temp)).isDirectory() && ifDeleteFolder) {
                deleteAllFile(path + File.separator + tempList[i],
                        ifDeleteFolder);// 先删除文件夹里面的文件
                deleteFolder(path + File.separator + tempList[i]);// 再删除空文件夹
            }
        }
    }

    /**
     * 删除文件夹,包括里面的文件
     *
     * @param folderPath 文件夹路径字符串
     */
    public static void deleteFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (myFilePath.exists()) {
                deleteAllFile(folderPath, true); // 删除完里面所有内容
                // 删除空文件夹
                if(!myFilePath.delete()){
                	log4j.error("删除文件失败！");
                }
            }
            log4j.info("ok!删除文件夹成功: " + folderPath);
        } catch (Exception e) {
            log4j.error("删除文件夹失败: " + folderPath, e);
        }
    }

    /**
     * 复制文件,如果目标文件的路径不存在,会自动新建路径
     *
     * @param sourcePath 源文件路径, e.g. "c:/cs.txt"
     * @param targetPath 目标文件路径 e.g. "f:/bb/cs.txt"
     */
    public static void copyFile(String sourcePath, String targetPath) {
        InputStream inStream = null;
        FileOutputStream fos = null;
        try {
            int byteSum = 0;
            int byteRead = 0;
            File sourcefile = new File(sourcePath);
            if (sourcefile.exists()) { // 文件存在时
                inStream = new FileInputStream(sourcePath); // 读入原文件
                String dirPath = extractDirPath(targetPath); // 文件所在目录的路径
                if (dirPath != null) { // 如文件所在目录不存在则先建目录
                    makeFolder(dirPath);
                }
                fos = new FileOutputStream(targetPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; // 字节数 文件大小
                    fos.write(buffer, 0, byteRead);
                }
                log4j.info("要复制的文件路径-->" + sourcePath);
                log4j.info("目标文件路径-->" + targetPath);
                log4j.info("文件大小-->" + byteSum);

            }
        } catch (Exception e) {
            log4j.debug("复制文件出错: " + sourcePath, e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log4j.error("copyFile.IOException", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log4j.error("copyFile.IOException2", e);
                }
            }
        }
    }

    /**
     * 将路径和文件名拼接起来
     *
     * @param folderPath 某一文件夹路径字符串，e.g. "c:\cs\" 或 "c:\cs"
     * @param fileName   某一文件名字符串, e.g. "cs.txt"
     * @return 文件全路径的字符串
     */
    public static String makeFilePath(String folderPath, String fileName) {
        return folderPath.endsWith("\\") || folderPath.endsWith("/") ? folderPath
                + fileName
                : folderPath + File.separatorChar + fileName;
    }

    /**
     * 将某一文件夹下的所有文件和子文件夹拷贝到目标文件夹，若目标文件夹不存在将自动创建
     *
     * @param sourcePath 源文件夹字符串，e.g. "c:\cs"
     * @param targetPath 目标文件夹字符串，e.g. "d:\tt\qq"
     */
    public static void copyFolder(String sourcePath, String targetPath) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            /* 如果文件夹不存在 则建立新文件夹*/
            makeFolder(targetPath);
            String[] file = new File(sourcePath).list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                String tempPath = makeFilePath(sourcePath, file[i]);
                temp = new File(tempPath);
                String target = null;
                if (temp.isFile()) {
                    input = new FileInputStream(temp);
                    output = new FileOutputStream(target = makeFilePath(
                            targetPath, file[i]));
                    byte[] b = new byte[1024 * 5];
                    int len = 0;
                    int sum = 0;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                        sum += len;
                    }
                    output.flush();
                    closeInputStream(input);
                    closeFileOutputStream(output);

                    log4j.info("要复制的文件路径-->" + tempPath);
                    log4j.info("目标文件路径-->" + target);
                    log4j.info("文件大小-->" + sum);

                } else if (temp.isDirectory()) {
                    // 如果是子文件夹
                    copyFolder(sourcePath + '/' + file[i], targetPath + '/'
                            + file[i]);
                }
            }
        } catch (Exception e) {
            log4j.error("复制整个文件夹内容操作出错", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log4j.error("copyFolder.IOException", e);
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log4j.error("copyFolder.IOException2", e);
                }
            }
        }
    }

    /**
     * 移动文件
     *
     * @param oldFilePath 旧文件路径字符串, e.g. "c:\tt\cs.txt"
     * @param newFilePath 新文件路径字符串, e.g. "d:\kk\cs.txt"
     */
    public static void moveFile(String oldFilePath, String newFilePath) {
        copyFile(oldFilePath, newFilePath);
        deleteFile(oldFilePath);
    }

    /**
     * 移动文件夹
     *
     * @param oldFolderPath 旧文件夹路径字符串，e.g. "c:\cs"
     * @param newFolderPath 新文件夹路径字符串，e.g. "d:\cs"
     */
    public static void moveFolder(String oldFolderPath, String newFolderPath) {
        copyFolder(oldFolderPath, newFolderPath);
        deleteFolder(oldFolderPath);

    }

    /**
     * 获得某一文件夹下的所有文件的路径集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件的路径的字符串
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ArrayList getFilePathFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        try {
            File[] tempFile = file.listFiles();
            for (int i = 0; i < tempFile.length; i++) {
                if (tempFile[i].isFile()) {
                    String tempFileName = tempFile[i].getName();
                    fileNames.add(makeFilePath(filePath, tempFileName));
                }
            }
        } catch (Exception e) {
            fileNames.add("尚无文件到达！");
            log4j.error("getFilePathFromFolder.Exception", e);
        }
        return fileNames;
    }

    /**
     * 获得某一文件夹下的所有TXT，txt文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ArrayList getFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile()) {
                fileNames.add(tempFile[i].getName());
            }
        }
        return fileNames;
    }

    /**
     * 获得某一路径下所以文件夹名称的集合
     *
     * @param filePath
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ArrayList getFolderNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isDirectory()) {
                fileNames.add(tempFile[i].getName());
            }
        }
        return fileNames;
    }

    /**
     * 获得某一文件夹下的所有文件的总数
     *
     * @param filePath 文件夹路径
     * @return int 文件总数
     */
    public static int getFileCount(String filePath) {
        int count = 0;
        try {
            File file = new File(filePath);
            if (!isFolderExist(filePath)) {
                return count;
            }
            File[] tempFile = file.listFiles();
            for (int i = 0; i < tempFile.length; i++) {
                if (tempFile[i].isFile()) {
                    count++;
                }
            }
        } catch (Exception fe) {
            log4j.error("getFileCount.Exception", fe);
            count = 0;
        }
        return count;
    }

    /**
     * 获得某一路径下要求匹配的文件的个数
     *
     * @param filePath 文件夹路径
     * @param matchs   需要匹配的文件名字符串,如".*a.*",如果传空字符串则不做匹配工作
     *                 直接返回路径下的文件个数
     * @return int 匹配文件名的文件总数
     */
    public static int getFileCount(String filePath, String matchs) {
        int count = 0;
        if (!isFolderExist(filePath)) {
            return count;
        }
        if (StringUtil.isEmpty(matchs)) {
            return getFileCount(filePath);
        }
        File file = new File(filePath);
        log4j.info("filePath in getFileCount: " + filePath);
        log4j.info("matchs in getFileCount: " + matchs);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile() && Pattern.matches(matchs, tempFile[i].getName())) {
                count++;
            }
        }
        return count;
    }

    public static int getStrCountFromFile(String filePath, String str) {
        if (!isFileExist(filePath)) {
            return 0;
        }
        FileReader fr = null;
        BufferedReader br = null;
        int count = 0;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(str) != -1) {
                	count++;
                }
            }
        } catch (FileNotFoundException e) {
            log4j.error("getStrCountFromFile.FileNotFoundException", e);
        } catch (IOException e) {
            log4j.error("getStrCountFromFile.IOException", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                log4j.error("getStrCountFromFile.Exception", e);
            }
        }
        return count;
    }

    /**
     * 获得某一文件的行数
     *
     * @param filePath 文件夹路径
     * @return int 行数
     */
    @SuppressWarnings("unused")
    public static int getFileLineCount(String filePath) {
        if (!isFileExist(filePath)) {
            return 0;
        }
        FileReader fr = null;
        BufferedReader br = null;
        int count = 0;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                count++;
            }
        } catch (FileNotFoundException e) {
            log4j.error("getFileLineCount.FileNotFoundException", e);
        } catch (IOException e) {
            log4j.error("getFileLineCount.IOException", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                log4j.error("getFileLineCount.Exception", e);
            }
        }
        return count;
    }

    /**
     * 判断某一文件是否为空
     *
     * @param filePath 文件的路径字符串，e.g. "c:\cs.txt"
     * @return 如果文件为空返回true, 否则返回false
     * @throws IOException
     */
    public static boolean ifFileIsNull(String filePath) throws IOException {
        boolean result = false;
        FileReader fr = new FileReader(filePath);
        if (fr.read() == -1) {
            result = true;
            log4j.info(filePath + " 文件为空!");
        } else {
            log4j.info(filePath + " 文件不为空!");
        }
        fr.close();
        return result;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件路径字符串，e.g. "c:\cs.txt"
     * @return 若文件存在返回true, 否则返回false
     */
    public static boolean isFileExist(String fileName) {
        // 判断文件名是否为空
        if (fileName == null || fileName.length() == 0) {
            log4j.error("文件名长度为0");
            return false;
        } else {
            // 读入文件 判断文件是否存在
            File file = new File(fileName);
            if (!file.exists() || file.isDirectory()) {
                log4j.error(fileName + "不存在");
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件夹是否存在
     *
     * @param folderPath，文件夹路径字符串，e.g. "c:\cs"
     * @return 若文件夹存在返回true, 否则返回false
     */
    public static boolean isFolderExist(String folderPath) {
        File file = new File(folderPath);
        return file.isDirectory();
    }

    /**
     * 获得文件的大小
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件的大小, 单位kb, 如果文件不存在返回null
     */
    public static Double getFileSize(String filePath) {
        if (!isFileExist(filePath))
            return null;
        else {
            File file = new File(filePath);
            double intNum = Math.ceil(file.length() / 1024.0);
            return intNum;
        }

    }

    /**
     * 获得文件的大小,字节表示
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件的大小, 单位kb, 如果文件不存在返回null
     */
    public static Double getFileByteSize(String filePath) {
        if (!isFileExist(filePath)) {
            return null;
        }
        File file = new File(filePath);
        return Math.ceil(file.length());
    }

    /**
     * 获得外汇牌价文件的大小(字节)
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件的大小, 单位kb, 如果文件不存在返回null
     */
    public static Double getWhpjFileSize(String filePath) {
        if (!isFileExist(filePath)) {
            return null;
        }
        return (double) new File(filePath).length();
    }

    /**
     * 获得文件的最后修改时间
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件最后的修改日期的字符串, 如果文件不存在返回null
     */
    public static String fileModifyTime(String filePath) {
        if (!isFileExist(filePath)) {
            return null;
        }
        File file = new File(filePath);

        long timeStamp = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return formatter.format(new Date(timeStamp));
    }

    /**
     * 遍历某一文件夹下的所有文件,返回一个ArrayList,每个元素又是一个子ArrayList,
     * 子ArrayList包含三个字段,依次是文件的全路径(String),文件的修改日期(String),
     * 文件的大小(Double)
     *
     * @param folderPath, 某一文件夹的路径
     * @return ArrayList
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ArrayList getFilesSizeModifyTime(String folderPath) {
        List returnList = new ArrayList();
        List filePathList = getFilePathFromFolder(folderPath);
        for (int i = 0; i < filePathList.size(); i++) {
            List tempList = new ArrayList();
            String filePath = (String) filePathList.get(i);
            String modifyTime = fileModifyTime(filePath);
            Double fileSize = getFileSize(filePath);
            tempList.add(filePath);
            tempList.add(modifyTime);
            tempList.add(fileSize);
            returnList.add(tempList);
        }
        return (ArrayList) returnList;
    }

    /**
     * 获得某一文件夹下的所有TXT，txt文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ArrayList getTxtFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile() && (tempFile[i].getName().indexOf("TXT") != -1 || tempFile[i].getName().indexOf("txt") != -1)) {
                fileNames.add(tempFile[i].getName());
            }
        }
        return fileNames;
    }

    /**
     * 获得某一文件夹下的所有xml，XML文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ArrayList getXmlFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile() && (tempFile[i].getName().indexOf("XML") != -1 || tempFile[i].getName().indexOf("xml") != -1)) {
                fileNames.add(tempFile[i].getName());
            }
        }
        return fileNames;
    }


    /**
     * @param destFile
     * @param sourceFileName
     * @throws Exception
     * @作者: yanghao
     * @时间: Dec 22, 2015 4:21:55 PM
     * @描述: 将destFile中所有的文件复制到sourceFileName文件夹中，如果有则替换，如果没有则新建
     * @备注:
     */
    public static void copy(File destFile, String sourceFileName) throws Exception {
        //1. 获取该文件夹下的所有文件
        File[] files = destFile.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //2. 获取目标项目的绝对路径
                String sourceDic = new File(sourceFileName).getAbsolutePath();

                //3. 拼接目标文件的绝对路径
                String realSourceDic = sourceDic + "\\" + file.getName();
                File sourceFile = new File(realSourceDic);
                //4. 判断该源文件是否是文件夹
                if (file.isDirectory()) {
                    // 源文件是文件夹，再判断目标文件是否是文件夹，如果不是则新创建一个
                    if (!sourceFile.isDirectory()) {
                        sourceFile.mkdir();
                    }
                    //递归调用
                    copy(file, realSourceDic);
                } else {
                    // 源文件不是文件夹，则确定为文件，再判断目标文件是否存在，存在则删除
                    if (sourceFile.exists()) {
                        if(!sourceFile.delete()){
                        	log4j.error("删除文件失败！");
                        }
                    }
                    //拷贝文件
                    copyFile(file.getAbsolutePath(), sourceFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @param path
     * @作者: yanghao
     * @时间: Jan 13, 2016 3:29:32 PM
     * @描述: 创建文件夹
     * @备注:
     */
    public static void createDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * @param file
     * @return 每段脚本数组
     * @作者: xch
     * @时间: Dec 2, 2014 2:43:03 PM
     * @描述: mysql 文本格式的脚本
     * @备注:文件中注释 使用'#'号,每段脚本结束使用‘;’
     */
    public static String[] obtainScripts(File file) {
        String[] scriptAC = null;

        FileReader fr = null;
        BufferedReader bufferedReader = null;
        try {
            fr = new FileReader(file);
            bufferedReader = new BufferedReader(fr);
            StringBuilder sBuilder = new StringBuilder();

            String content = null;
            while ((content = bufferedReader.readLine()) != null) {
                if (!content.trim().startsWith("#")) {
                    sBuilder.append(content);
                }
            }

            if (sBuilder.length() > 0) {
                String scripts = sBuilder.toString();
                scriptAC = scripts.split(";");
            }
        } catch (Exception e) {
            log4j.error("obtainScripts.Exception", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log4j.error("obtainScripts.IOException", e);
                }
            }

            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    log4j.error("obtainScripts.IOException2", e);
                }
            }
        }
        return scriptAC;
    }
}
