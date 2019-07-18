package com.lk.redo.commons.util.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
