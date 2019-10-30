package com.purpleshine.general.helpers;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Lists;

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
        return objectMapper.<T>treeToValue(node, _class);
    }
    
    /**
     * 將資料自動轉成 T
     * @param data
     * @param _class
     * @return
     * @throws IOException 
     */
    static public <T> T convertNodeToObject(final JsonNode node, final TypeReference<? extends T> typeReference) throws IOException {
        return objectMapper.readValue(objectMapper.treeAsTokens(node), typeReference);
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
    
    /**
     * 排序內容
     * 
     * @param tree
     */
    static public void sort(JsonNode tree) {
        if (tree.isObject()) {
            sortObject(tree);
        } else if (tree.isArray()) {
            sortArray(tree);
        }
    }
    
    /**
     * 排序物件
     * 
     * @param tree
     */
    static public void sortObject(JsonNode tree) {
        List<String> asList = Lists.newArrayList(tree.fieldNames());
        Collections.sort(asList);
        LinkedHashMap<String, JsonNode> map = new LinkedHashMap<String, JsonNode>();
        for (String f : asList) {
            JsonNode value = tree.get(f);
            map.put(f, value);
        }
        ((ObjectNode) tree).removeAll();
        ((ObjectNode) tree).setAll(map);
    }
    
    /**
     * 排序array
     * 
     * @param tree
     */
    static public void sortArray(JsonNode tree) {
        for (JsonNode jsonNode : tree) {
            sort(jsonNode);
        }

        List<JsonNode> list = Lists.newArrayList(((ArrayNode) tree).iterator());
        Collections.sort(list, new JsonNodeComparator());
        ((ArrayNode) tree).removeAll();
        ((ArrayNode) tree).addAll(list);
    }
    
    public static class JsonNodeComparator implements Comparator<JsonNode> {
        public int compare(JsonNode o1, JsonNode o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }

            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            if (o1.isObject() && o2.isObject()) {
                return compObject(o1, o2);
            } else if (o1.isArray() && o2.isArray()) {
                return compArray(o1, o2);
            } else if (o1.isValueNode() && o2.isValueNode()) {
                return compValue(o1, o2);
            } else {
                return 1;
            }
        }

        private int compValue(JsonNode o1, JsonNode o2) {

            if (o1 == o2 || o1.isNull() && o2.isNull()) {
                return 0;
            }
            
            if (o1.isNull()) {
                return -1;
            }

            if (o2.isNull()) {
                return 1;
            }
            
            if (o1.isNumber() && o2.isNumber()) {
                return o1.bigIntegerValue().compareTo(o2.bigIntegerValue());
            }

            if (o1.isBoolean() && o2.isBoolean()) {
                return Boolean.compare(o1.asBoolean(), o2.asBoolean());
            }
            
            return o1.asText().compareTo(o2.asText());
        }

        private int compArray(JsonNode o1, JsonNode o2) {

            int c = ((ArrayNode) o1).size() - ((ArrayNode) o2).size();
            if (c != 0) {
                return c;
            }
            for (int i = 0; i < ((ArrayNode) o1).size(); i++) {
                c = compare(o1.get(i), o2.get(i));
                if (c != 0) {
                    return c;
                }
            }

            return 0;
        }

        private int compObject(JsonNode o1, JsonNode o2) {

            String id1 = o1.get("id") == null ? null : o1.get("id").asText();
            String id2 = o2.get("id") == null ? null : o2.get("id").asText();
            if (id1 != null) {
                int c = id1.compareTo(id2);
                if (c != 0) {
                    return c;
                }
            }
            int c = ((ObjectNode) o1).size() - ((ObjectNode) o2).size();
            if (c != 0) {
                return c;
            }

            Iterator<String> fieldNames1 = ((ObjectNode) o1).fieldNames();
            Iterator<String> fieldNames2 = ((ObjectNode) o2).fieldNames();
            for (; fieldNames1.hasNext();) {
                String f = fieldNames1.next();

                c = f.compareTo(fieldNames2.next());
                if (c != 0) {
                    return c;
                }

                JsonNode n1 = o1.get(f);
                JsonNode n2 = o2.get(f);
                c = compare(n1, n2);
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }
    }
}