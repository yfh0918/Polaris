package com.polaris.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.CollectionFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

public abstract class YamlUtil {

	public static Properties getProperties (String fileName,boolean includePath, boolean includeClassPath) {
		if (includePath) {
			try (InputStream in = FileUitl.getStreamFromPath(fileName)) {
				if (in != null) {
					return getProperties(in);
			    }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		if (includeClassPath) {
			try (InputStream in = FileUitl.getStreamFromClassPath(fileName)) {
				if (in != null) {
					return getProperties(in);
			    }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return null;
	}
	
	public static Properties getProperties (InputStream inputStream) throws IOException {
		Yaml yaml = createYaml();
		Resource resource = new InputStreamResource(inputStream);
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		try (Reader reader = new UnicodeReader(resource.getInputStream())) {
			for (Object object : yaml.loadAll(reader)) {
				if (object != null) {
					properties.putAll(PropertyUtil.getProperties(asMap(object)));
				}
			}
			
		} 
		return properties;
	}
	
	public static Properties getProperties(String fileContent) {
		Yaml yaml = createYaml();
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		for (Object object : yaml.loadAll(fileContent)) {
			if (object != null) {
				properties.putAll(PropertyUtil.getProperties(asMap(object)));
			}
		}
		return properties;
	}
	
	private static Yaml createYaml() {
		LoaderOptions options = new LoaderOptions();
		options.setAllowDuplicateKeys(false);
		return new Yaml(options);
	}
	
	@SuppressWarnings("unchecked")
	private static  Map<String, Object> asMap(Object object) {
		// YAML can have numbers as keys
		Map<String, Object> result = new LinkedHashMap<>();
		if (!(object instanceof Map)) {
			// A document can be a text literal
			result.put("document", object);
			return result;
		}

		Map<Object, Object> map = (Map<Object, Object>) object;
		map.forEach((key, value) -> {
			if (value instanceof Map) {
				value = asMap(value);
			}
			if (key instanceof CharSequence) {
				result.put(key.toString(), value);
			}
			else {
				// It has to be a map key in this case
				result.put("[" + key.toString() + "]", value);
			}
		});
		return result;
	}
	

}
