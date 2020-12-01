package com.polaris.demo;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;


public final class VersionUpdate {
	public static void main(String[] args) throws Exception { 
		String oldChar = "<version>1.7.2</version>";
		String newChar = "<version>1.7.3</version>";
		String oldChar2 = "<polaris-version>1.7.2</polaris-version>";
		String newChar2 = "<polaris-version>1.7.3</polaris-version>";
		List<String> scanFiles = new ArrayList<String>();
        scanFilesWithRecursion(scanFiles, "C:\\projects\\mayun\\Polaris", "pom.xml");
        for (String file : scanFiles) {
        	if (file.contains("target"+File.separator+"classes")) {
        		continue;
        	}
        	List<String> lines = FileUtil.readLines(new File(file),Charset.defaultCharset());
        	String source = lines.get(5);
        	System.out.println(file + " before:"+source);
        	source = source.replace(oldChar, newChar);
        	System.out.println(file + " after:"+source);
        	lines.set(5, source);
        	FileUtil.writeLines(lines, new File(file), Charset.defaultCharset());
        	if (file.contains("Polaris_parent")) {
            	String source2 = lines.get(19);
            	System.out.println(file + " before2:"+source2);
            	source2 = source2.replace(oldChar2, newChar2);
            	System.out.println(file + " after2:"+source2);
            	lines.set(19, source2);
            	FileUtil.writeLines(lines, new File(file), Charset.defaultCharset());
        	}
        }
	}
	
	public static void scanFilesWithRecursion(List<String> scanFiles, String folderPath, String matchKey) throws Exception{
		File directory = new File(folderPath);
		if(directory.isDirectory()){
			File [] filelist =directory.listFiles();
			
			for(int i = 0; i < filelist.length; i ++){
				/**如果当前是文件夹，进入递归扫描文件夹**/
				if(filelist[i].isDirectory()){
					/**递归扫描下面的文件夹**/
					scanFilesWithRecursion(scanFiles, filelist[i].getAbsolutePath(),matchKey);
				}
				/**非文件夹**/
				else{
					if (filelist[i].getAbsolutePath().endsWith(matchKey)) {
						scanFiles.add(filelist[i].getAbsolutePath());
					}
				}
			}
		}
	}
}
