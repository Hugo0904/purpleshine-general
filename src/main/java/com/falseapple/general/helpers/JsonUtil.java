package com.falseapple.general.helpers;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class JsonUtil {
    static private final ObjectMapper objectMapper = new ObjectMapper()
//          .setSerializationInclusion(Include.NON_NULL) // 迴避null
            .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)  // private欄位強制取得
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 忽略未定義的欄位
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 取消將日期轉換為TIMESTAMPS
    
    static private final ObjectMapper xmlMapper = new XmlMapper()
//          .registerModule(new SimpleModule().addDeserializer(Object.class, new FixedUntypedObjectDeserializer()))
//          .setSerializationInclusion(Include.NON_NULL) // 迴避null
            .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)  // private欄位強制取得
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 忽略未定義的欄位
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 取消將日期轉換為TIMESTAMPS
    
    /**
     * 建立一個空的ObjectNode
     * @return
     */
    static public ObjectNode createObjectNode() {
//      JsonNodeFactory.instance.objectNode();
        return objectMapper.createObjectNode();
    }
    
    /**
     * 建立一個空的ArrayNode
     * @return
     */
    static public ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }
    
    /**
     * 將資料轉換成JsonNode
     * @param obj 要轉換的資料
     * @return 轉換成功的JsonNode
     */
    static public JsonNode convertJsonTree(final Object obj) {
        if (obj instanceof JsonNode) {
            return (JsonNode) obj;
        }
        
        return objectMapper.valueToTree(obj);
    }
    
    /**
     * 將node內的資料映射至object上
     * @param node
     * @param into
     * @throws JsonProcessingException
     * @throws IOException
     */
    static public void dyamicPopulate(final JsonNode node, final Object into) throws JsonProcessingException, IOException {
        objectMapper.readerForUpdating(into).readValue(node);
    }

    /**
     * 將Json資料自動轉成JsonNode
     * @param json
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    static public JsonNode toNode(final String json) throws JsonProcessingException, IOException {
        return objectMapper.readTree(json);
    }
    
    /**
     * 將Json資料自動轉成JsonNode
     * @param json
     * @return
     */
    static public Optional<JsonNode> toNodeOptional(final String json) {
        try {
            return Optional.of(toNode(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * 將資料自動轉成 T
     * @param data
     * @param _class
     * @return
     * @throws JsonProcessingException 
     */
    static public <T> T convertNodeToObject(final JsonNode node, final Class<T> _class) throws JsonProcessingException {
        return objectMapper.treeToValue(node, _class);
    }
    
    /**
     * 
     * @param node
     * @param _class
     * @return
     */
    static public <T> Optional<T> convertNodeToObjectOptional(final JsonNode node, final Class<T> _class) {
        try {
            return Optional.of(convertNodeToObject(node, _class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * 將資料自動轉成 T
     * @param data
     * @param _class
     * @return
     * @throws IOException 
     */
    static public <T> T convertJsonToObject(final String json, final Class<T> _class) throws IOException {
        return objectMapper.readValue(json, _class);
    }
    
    /**
     * 
     * @param json
     * @param _class
     * @return
     */
    static public <T> Optional<T> convertJsonToObjectOptional(final String json, final Class<T> _class) {
        try {
            return Optional.of(convertJsonToObject(json, _class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * 
     * @param object
     * @return
     */
    static public String convertObjectToJson(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param object
     * @return
     */
    static public byte[] convertObjectToByte(final Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param object
     * @return
     */
    static public Optional<String> convertObjectToJsonOptional(final Object object) {
        try {
            return Optional.of(convertObjectToJson(object));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * 將Xml資料轉換成JsonNode
     * @param xml
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    static public JsonNode readXml(final String xml) throws JsonProcessingException, IOException {
        // 官方這方式 這個方式如果有array(elements) 會lose
//      return xmlMapper.readTree(xml);
        
        final JSONObject jsonObject = XML.toJSONObject(xml);
        return toNode(jsonObject.toString());
    }
    
    /**
     * 
     * @param xml
     * @param _class
     * @return
     */
    static public <T> Optional<T> readXmlOptional(final String xml, final Class<T> _class) {
        try {
            return Optional.of(xmlMapper.treeToValue(readXml(xml), _class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    
}