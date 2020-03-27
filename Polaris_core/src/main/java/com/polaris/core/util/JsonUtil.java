package com.polaris.core.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import cn.hutool.json.JSONObject;

public abstract class JsonUtil {
	private final static Type mapType = new TypeToken<Map<String, String>>(){}.getType();
	private final static Gson gson = new GsonBuilder()
							.registerTypeAdapter(mapType, new JsonMapSerializer())
							.create();
	
	public static void toBeanFromProperties(Object target, Map<String, String> bundleMap,boolean ignoreError) {
		String json = gson.toJson(bundleMap, mapType);
		JSONObject hutJson = new JSONObject(json);
		hutJson.toBean(target,ignoreError);
	}
	public static void toBean(Object target, String json,boolean ignoreError) {
		JSONObject hutJson = new JSONObject(json);
		hutJson.toBean(target,ignoreError);
	}
	
	public static class JsonMapSerializer implements JsonSerializer<Map<String, String>> {

	    @Override
	    public JsonElement serialize(final Map<String, String> bundleMap, final Type typeOfSrc, final JsonSerializationContext context) {
	        final JsonObject resultJson =  new JsonObject();

	        for (final String key: bundleMap.keySet()) {
	            try {
	                createFromBundleKey(resultJson, key, bundleMap.get(key));
	            } catch (final IOException e) {
	            }
	        }

	        return resultJson;
	    }
	    
	    public static JsonElement createFromBundleKey(final JsonObject resultJson, final String key, final Object value) throws IOException {
	        if (!key.contains(".")) {
	        	addElement(resultJson, key, value);
	            
	            return resultJson;
	        }

	        final String currentKey = firstKey(key);
	        if (currentKey != null) {
	            final String subRightKey = key.substring(currentKey.length() + 1, key.length());
	            final JsonObject childJson = getJsonIfExists(resultJson, currentKey);
	            addElement(resultJson, currentKey, createFromBundleKey(childJson, subRightKey, value));
	        }

	        return resultJson;
	    }

        private static String firstKey(final String fullKey) {
            final String[] splittedKey = fullKey.split("\\.");

            return (splittedKey.length != 0) ? splittedKey[0] : fullKey;
        }

        private static JsonObject getJsonIfExists(final JsonObject parent, final String key) {
            if (parent == null) {
                return null;
            }

            if (parent.get(key) != null && !(parent.get(key) instanceof JsonObject)) {
            	return new JsonObject();
            }

            if (parent.getAsJsonObject(key) != null) {
                return parent.getAsJsonObject(key);
            } else {
                return new JsonObject();
            }
       }
        
       private static void addElement(JsonObject jsonObject, String key, Object obj) {
    	   String[] splittedKey = key.split("\\[");
    	   if (!isArray(splittedKey)) {
    		   if (obj instanceof String) {
        		   jsonObject.addProperty(key, obj == null?null:obj.toString());
        	   } else {
        		   jsonObject.add(key, (JsonElement)obj);
        	   }
    	   } else {
    		   key = splittedKey[0];
			   JsonElement element = jsonObject.get(key);
			   if (element == null) {
				   element = new JsonArray();
				   jsonObject.add(key, element);
			   }
    		   if (obj instanceof String) {
				   ((JsonArray)element).add(obj == null?null:obj.toString());
        	   } else {
        		   ((JsonArray)element).add((JsonElement)obj);
        	   }
    	   }
    	   
       }
       
       private static boolean isArray(String[] splittedKey) {
    	   if (splittedKey.length == 2) {
    		   String tempKey = splittedKey[1].trim();
    		   if (tempKey.endsWith("]") ) {
    			   try {
    				   Integer.parseInt(tempKey.substring(0, tempKey.length() - 1));
    				   return true;
    			   } catch (Exception ex) {
    			   } 
    		   }
    		   
    	   }
    	   return false;
       }
        

	}

}
