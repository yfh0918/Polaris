package com.polaris.core.config.reader.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.polaris.core.config.reader.ConfReaderFactory;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.StringUtil;

/**
 * get file info from fileName according to strategy rule
 * strategy rule  : 
 *    first order : local path config/fileName  
 *    second order: local path fileName
 *    third order : classpath config/fileName
 *    fourth order: classpath fileName
 */
public class ConfLauncherReaderStrategy {
	public static final ConfLauncherReaderStrategy INSTANCE = new ConfLauncherReaderStrategy();
	private static String CONFIG = "config";
	private ConfLauncherReaderStrategy() {}

	public String getContents (String fileN) {
		try (InputStream in = getInputStream_0(fileN)) {
			if (in != null) {
				return FileUtil.read(in);
		    }
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
		try (InputStream in = getInputStream_1(fileN)) {
			if (in != null) {
				return FileUtil.read(in);
		    }
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
		try (InputStream in = getInputStream_2(fileN)) {
			if (in != null) {
				return FileUtil.read(in);
		    }
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
		try (InputStream in = getInputStream_3(fileN)) {
			if (in != null) {
				return FileUtil.read(in);
		    }
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
		return null;
	}

	public Properties getProperties (String fileName) {
		
		//no suffix
		List<String> fileList = new ArrayList<>();
		String fileSuffix = FileUtil.getSuffix(fileName);
		if (StringUtil.isEmpty(fileSuffix)) {
			for (String suffix : ConfReaderFactory.SUPPORT_TYPE) {
				fileList.add(fileName + FileUtil.DOT + suffix);
	    	}
		} else {
			fileList.add(fileName);
		}
		
		//files
		String[] fileNames = fileList.toArray(new String[fileList.size()]);
		
		//from path
		for (String fileN : fileNames) {
			try (InputStream in = getInputStream_0(fileN)) {
				if (in != null) {
					return ConfReaderFactory.get(fileN).getProperties(in);
			    }
		    } catch (IOException ex) {
		    	ex.printStackTrace();
		    }
		}
		for (String fileN : fileNames) {
			try (InputStream in = getInputStream_1(fileN)) {
				if (in != null) {
					return ConfReaderFactory.get(fileN).getProperties(in);
			    }
		    } catch (IOException ex) {
		    	ex.printStackTrace();
		    }
		}
		
		//from class-path
		for (String fileN : fileNames) {
			try (InputStream in = getInputStream_2(fileN)) {
				if (in != null) {
					return ConfReaderFactory.get(fileN).getProperties(in);
			    }
			} catch (IOException ex) {
		    	ex.printStackTrace();
		    }
			
		}
		for (String fileN : fileNames) {
			try (InputStream in = getInputStream_3(fileN)) {
				if (in != null) {
					return ConfReaderFactory.get(fileN).getProperties(in);
			    }
			} catch (IOException ex) {
		    	ex.printStackTrace();
		    }
		}
		
		
		return null;
	}
	
	public InputStream getInputStream (String fileName) {
		InputStream inputStream = getInputStream_0(fileName);
		if (inputStream != null) {
			return inputStream; 
		}
		inputStream = getInputStream_1(fileName);
		if (inputStream != null) {
			return inputStream; 
		}
		inputStream = getInputStream_2(fileName);
		if (inputStream != null) {
			return inputStream; 
		}
		return getInputStream_3(fileName);
	}
	private InputStream getInputStream_0 (String fileName) {
		File file = getFile_0(fileName);
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	private InputStream getInputStream_1 (String fileName) {
		File file = getFile_1(fileName);
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	private InputStream getInputStream_2 (String fileName) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(File.separator + CONFIG + File.separator + fileName);
		if (in != null) {
			return in;
	    }
		return null;
	}
	private InputStream getInputStream_3 (String fileName) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
		if (in != null) {
			return in;
	    }
		return null;
	}

	public File getFile(String fileName) {
		File file = getFile_0(fileName);
		if (file != null) {
			return file;
		}
		return getFile_1(fileName);
	}
	private File getFile_0(String fileName) {
		try {
			String path = FileUtil.getFullPath("");
			File file = new File(path + File.separator + CONFIG + File.separator + fileName);
			if (file.exists()) {
				return file;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	private File getFile_1(String fileName) {
		try {
			String path = FileUtil.getFullPath("");
			File file = new File(path + File.separator + fileName);
			if (file.exists()) {
				return file;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
