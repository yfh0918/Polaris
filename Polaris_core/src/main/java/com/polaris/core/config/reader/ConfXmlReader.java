package com.polaris.core.config.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.polaris.core.util.FileUitl;

public class ConfXmlReader implements ConfReader{

	@Override
	public Properties getProperties(String fileName, boolean includePath, boolean includeClassPath) {
		//path
		if (includePath) {
			try (InputStream in = FileUitl.getStreamFromPath(fileName)) {
				if (in != null) {
					return get(in);
			    }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		
		//classpath
		if (includeClassPath) {
			try (InputStream in = FileUitl.getStreamFromClassPath(fileName)) {
				if (in != null) {
					return get(in);
			    }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		return null;
	}

	@Override
	public Properties getProperties(String contectLines) {
		try (InputStream  inputStream = new ByteArrayInputStream(contectLines.getBytes())) {
			return get(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Properties get(InputStream inputStream) throws Exception {
		Properties properties = new Properties();
		properties.loadFromXML(inputStream);
		return properties;
	}

}
