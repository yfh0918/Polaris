package com.polaris.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.CollectionFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
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
					properties.putAll(process(asMap(object)));
				}
			}
			
		} 
		return properties;
	}
	
	public static Map<String, Object> getMap(String lines) {
		Yaml yaml = createYaml();
		Map<String, Object> yamlMap = new HashMap<>();
		for (Object object : yaml.loadAll(lines)) {
			if (object != null) {
				yamlMap.putAll(getFlattenedMap(asMap(object)));
			}
		}
		return yamlMap;
	}
	
	public static Properties getProperties(String lines) {
		Yaml yaml = createYaml();
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		for (Object object : yaml.loadAll(lines)) {
			if (object != null) {
				properties.putAll(process(asMap(object)));
			}
		}
		return properties;
	}
	
	private static Properties process(Map<String, Object> map) {
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		properties.putAll(getFlattenedMap(map));
		return properties;
	}
	
	protected static Yaml createYaml() {
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
	
	protected static final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
		Map<String, Object> result = new LinkedHashMap<>();
		buildFlattenedMap(result, source, null);
		return result;
	}

	private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
		source.forEach((key, value) -> {
			if (StringUtils.hasText(path)) {
				if (key.startsWith("[")) {
					key = path + key;
				}
				else {
					key = path + '.' + key;
				}
			}
			if (value instanceof String) {
				
				result.put(key, value);
			}
			else if (value instanceof Map) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				buildFlattenedMap(result, map, key);
			}
			else if (value instanceof Collection) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) value;
				if (collection.isEmpty()) {
					result.put(key, "");
				}
				else {
					int count = 0;
					for (Object object : collection) {
						buildFlattenedMap(result, Collections.singletonMap(
								"[" + (count++) + "]", object), key);
					}
				}
			}
			else {
				result.put(key, (value != null ? value.toString() : ""));
			}
		});
	}
}
