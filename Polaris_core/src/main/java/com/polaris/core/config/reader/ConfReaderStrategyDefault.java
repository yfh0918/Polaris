package com.polaris.core.config.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.polaris.core.util.FileUtil;

public class ConfReaderStrategyDefault implements ConfReaderStrategy {
	public static final ConfReaderStrategy INSTANCE = new ConfReaderStrategyDefault();
	private static String CONFIG = "config";
	private ConfReaderStrategyDefault() {}

	@Override
	public Properties getProperties (String fileName, ConfReader confReader) {
		
		//file-path
		File file = getFile(fileName);
		if (file != null) {
			try (InputStream in = new FileInputStream(file)) {
				if (in != null) {
					return confReader.getProperties(in);
			    }
		    } catch (IOException ex) {
		    	ex.printStackTrace();
		    }
		}
		
		//from class-path
		try (InputStream in = getInputStream(fileName)) {
			if (in != null) {
				return confReader.getProperties(in);
		    }
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
		return null;
	}
	
	@Override
	public InputStream getInputStream (String fileName) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(File.separator + CONFIG + File.separator + fileName);
		if (in != null) {
			return in;
	    }
		in = this.getClass().getClassLoader().getResourceAsStream(fileName);
		if (in != null) {
			return in;
	    }
		return null;
	}
	
	@Override
	public File getFile (String fileName) {
		try {
			String path = FileUtil.getFullPath("");
			File file = new File(path + File.separator + CONFIG + File.separator + fileName);
			if (file.exists()) {
				return file;
			} else {
				file = new File(path + File.separator + fileName);
				if (file.exists()) {
					return file; 
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
