package com.lk.redo.commons.util.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2018/1/19 10:48
 * <p>
 * Description: [枚举帮助类，提供防止code重复，根据code获取枚举]
 * <p>
 * Company: [尚德机构]
 *
 * @author [刘辉]
 */
public class EnumUtil {
    private static Map<String, Object> cache = new ConcurrentHashMap<>();


    public static <T extends Enum> void putEnum(String code, T t) {
        String key = t.getClass().toString().concat("_").concat(code);
        if (cache.containsKey(key)) {
            throw new IllegalStateException("code 已存在！" + t);
        }
        cache.put(key, t);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> T getEnum(Class<T> clazz, String code) {
        return (T) cache.get(clazz.toString().concat("_").concat(code));
    }

}
