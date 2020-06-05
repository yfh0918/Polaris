/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polaris.core.util;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.polaris.core.exception.DeserializationException;
import com.polaris.core.exception.SerializationException;

public final class JacksonUtil {

	static ObjectMapper mapper = new ObjectMapper();

	static {
	    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		      .setSerializationInclusion(Include.NON_NULL);
	}

	public static String toJson(Object obj, ObjectMapper... objectMapper) {
        try {
            return getMapper(objectMapper).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }

	public static byte[] toJsonBytes(Object obj, ObjectMapper... objectMapper) {
        try {
            return ByteUtil.toBytes(getMapper(objectMapper).writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }

	public static <T> T toObj(byte[] json, Class<T> cls, ObjectMapper... objectMapper) {
        try {
            return toObj(StringUtil.newString4UTF8(json), cls);
        } catch (Exception e) {
            throw new DeserializationException(cls, e);
        }
    }

	public static <T> T toObj(byte[] json, Type cls, ObjectMapper... objectMapper) {
        try {
            return toObj(StringUtil.newString4UTF8(json), cls);
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }

    public static <T> T toObj(byte[] json, TypeReference<T> typeReference, ObjectMapper... objectMapper) {
        try {
            return toObj(StringUtil.newString4UTF8(json), typeReference);
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }

	public static <T> T toObj(String json, Class<T> cls, ObjectMapper... objectMapper) {
        try {
            return getMapper(objectMapper).readValue(json, cls);
        } catch (IOException e) {
            throw new DeserializationException(cls, e);
        }
    }

	public static <T> T toObj(String json, Type type, ObjectMapper... objectMapper) {
        try {
            return getMapper(objectMapper).readValue(json, getMapper(objectMapper).constructType(type));
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    public static <T> T toObj(String json, TypeReference<T> typeReference, ObjectMapper... objectMapper) {
        try {
            return getMapper(objectMapper).readValue(json, typeReference);
        } catch (IOException e) {
            throw new DeserializationException(typeReference.getClass(), e);
        }
    }

    public static JsonNode toObj(String json, ObjectMapper... objectMapper) {
        try {
            return getMapper(objectMapper).readTree(json);
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

	public static void registerSubtype(Class<?> clz, String type, ObjectMapper... objectMapper) {
	    getMapper(objectMapper).registerSubtypes(new NamedType(clz, type));
    }

    public static ObjectNode createEmptyJsonNode(ObjectMapper... objectMapper) {
	    return new ObjectNode(getMapper(objectMapper).getNodeFactory());
    }

    public static ArrayNode createEmptyArrayNode(ObjectMapper... objectMapper) {
	    return new ArrayNode(getMapper(objectMapper).getNodeFactory());
    }

    public static JsonNode transferToJsonNode(Object obj, ObjectMapper... objectMapper) {
	    return getMapper(objectMapper).valueToTree(obj);
    }
    
    private static ObjectMapper getMapper(ObjectMapper... objectMapper) {
        return objectMapper.length == 0 ? mapper : objectMapper[0];
    }
    
}
