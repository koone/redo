package com.lk.redo.commons.util.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeParser;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Created on 2018/5/15 19:24
 * <p>
 * Description: []
 * <p>
 * Company: [尚德机构]
 *
 * @author [刘辉]
 */
@Slf4j
public class JsonUtil {


    private static ObjectMapper objectMapper;
    private static TypeParser typeParser;
    static{
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //字段为NULL的时候不会列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 加入Java8 的时间类格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        typeParser = new TypeParser(objectMapper.getTypeFactory());
    }

    /**
     * 将Type转化为Jackson javatype name
     * @param type
     * @return
     */
    public static String toCanonical(Type type) {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        return typeFactory.constructType(type).toCanonical();
    }

    /**
     * 将数据根据javaType名称做反序列化
     * @param <T>
     * @param source
     * @param javaTypeStr
     * @return
     */
    public static  <T> T toBeanWithCanonical(String source, String javaTypeStr){
        if(null == source || "null".equals(source)){
            return null;
        }
        try {
            JavaType javaType = typeParser.parse(javaTypeStr);
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~", e);
        }
    }

    public static Class[] toRawType(String javaTypeListJson){
        if(null == javaTypeListJson){
            return null;
        }
        List<String> javaTypeStrList = JsonUtil.toList(javaTypeListJson, String.class);
        Class[] types = new Class[javaTypeStrList.size()];
        for (int i=0; i< javaTypeStrList.size(); i++){
            JavaType javaType = typeParser.parse(javaTypeStrList.get(i));
            types[i] = javaType.getRawClass();
        }
        return types;
    }

    public static  <T>  String toJson(T t){
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("bean to json error!~" , e);
        }
    }

    public static  <T>  T toBean(String source , Class<T> clazz){
        try {
            return objectMapper.readValue(source, clazz);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~" , e);
        }
    }

    public static  <T>  T[] toArray(String json , Class<T> clazz){
        if(StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructArrayType(clazz));
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~" , e);
        }
    }

    public static <T> List<T> toList(String json , Class<T> clazz) {
        if(StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return objectMapper.readValue(json , objectMapper.getTypeFactory().constructParametricType(List.class, clazz));
        } catch (IOException e) {
            log.error("pase json error! , "+json);
            throw new RuntimeException(e);
        }
    }

    public static <T> Collection<T> toCollection(String json , Class collectionClazz, Class<T> clazz) {
        if(StringUtils.isEmpty(json)){
            return null;
        }
        if(collectionClazz.isAssignableFrom(Collection.class)){
            throw new IllegalArgumentException("not a collection class");
        }
        try {
            return objectMapper.readValue(json , objectMapper.getTypeFactory().constructParametricType(collectionClazz, clazz));
        } catch (IOException e) {
            log.error("pase json error! , "+json);
            throw new RuntimeException(e);
        }
    }

    public static <K,V> Map<K,V> toMap(String json , Class mapClazz, Class<K> keyClass , Class<V> valueClass) {
        if(StringUtils.isEmpty(json)){
            return null;
        }
        if(mapClazz.isAssignableFrom(Map.class)){
            throw new IllegalArgumentException("not a map class");
        }
        try {
            return objectMapper.readValue(json , objectMapper.getTypeFactory().constructParametricType(mapClazz, keyClass,valueClass));
        } catch (IOException e) {
            log.error("pase json error! , "+json);
            throw new RuntimeException(e);
        }
    }

    public static <K,V> Map<K,V> toMap(String json , Class<K> keyClass , Class<V> valueClass) {
        if(StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return objectMapper.readValue(json , objectMapper.getTypeFactory().constructParametricType(Map.class, keyClass,valueClass));
        } catch (IOException e) {
            log.error("pase json error! , "+json);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        System.out.println(toMap("{\"a\":\"111\"}" , String.class,Object.class));
        System.out.println(JsonUtil.toJson(LocalDate.now()));
        System.out.println(JsonUtil.toJson(LocalTime.now()));
        System.out.println(JsonUtil.toJson(LocalDateTime.now()));
    }
}
