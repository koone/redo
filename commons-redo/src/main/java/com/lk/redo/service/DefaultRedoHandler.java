package com.lk.redo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.lk.redo.commons.util.utils.JsonUtil;
import com.lk.redo.model.RedoException;
import com.lk.redo.model.SysRedo;
import com.lk.redo.util.RedoCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 默认内置的redo处理器,通过将handlerName设置为defaultRedoHandler，来使用该handler<br>
 * 该处理器只能处理通过aop方式接入的redo事件类型
 * 注意：
 *  1.由于使用的是springContext get bean的方式，必须确保同类型的bean只有一个<br>
 *  2.由于是通过反射恢复方法执行现场，所以在使用的时候必须保证方法可以通过反射恢复<br>
 *
 */
@Service("defaultRedoHandler")
public class DefaultRedoHandler extends ApplicationObjectSupport implements RedoHandler {

    @Override
    public void redo(SysRedo redoItem) {
        RedoCheckUtils.check((null == redoItem || redoItem.getBizInvokeClazz() == null || redoItem.getBizInvokeMethod() == null),
                "bizInvokeClazz is null, and cannot be handled by defaultRedoHandler");
        Class<?> bizClazz = null;
        try {
            bizClazz = Class.forName(redoItem.getBizInvokeClazz());
        } catch (ClassNotFoundException e) {
            throw new RedoException("failed to create biz clazz for " + redoItem);
        }
        // get biz bean instance
        Object bizBean = getBeanInstence(bizClazz);
        // get method declared args
        String methodArgTypeStr = redoItem.getBizInvokeMethodArgtype();
        Class[] methodArgTypes= JsonUtil.toRawType(methodArgTypeStr);
        // get method
        Method m = null;
        try {
            m = bizClazz.getDeclaredMethod(redoItem.getBizInvokeMethod(), methodArgTypes);
        } catch (Exception e) {
            throw new RedoException("failed to get biz method for " + redoItem, e);
        }
        try {
            // invoke
            Object[] args = deserialize(redoItem.getBizInvokeArgs(), methodArgTypeStr);
            m.invoke(bizBean, args);
        } catch (Exception e) {
            throw new RedoException("failed to invoke biz method for " + redoItem, e);
        }
    }

    /**
     * 将getBizInvokeArgsStr()序列化得到的json array string，反序列化
     *
     * @see com.lk.redo.service.RedoService#serialize(Object[])
     * @param bizInvokeArgsStr 序列化的json array string 参数值
     * @param methodArgTypeStr 序列化的json array string 参数类型
     * @return argValue pair
     */
    public Object[] deserialize(String bizInvokeArgsStr, String methodArgTypeStr) {
        if (StringUtils.isBlank(bizInvokeArgsStr) || StringUtils.isBlank(methodArgTypeStr)) {
            return null;
        }
        List<JsonNode> argNameAndValueList = JsonUtil.toList(bizInvokeArgsStr, JsonNode.class);
        List<String> methodArgTypeList = JsonUtil.toList(methodArgTypeStr, String.class);
        /*if (mehtodArgTypeList.isEmpty()) { // no arg method
            return null;
        }*/
        if (argNameAndValueList.size() != methodArgTypeList.size()) {
            throw new RedoException("failed to deserialize param");
        }
        List<Object> args = new ArrayList<Object>();
        for (int i = 0; i < argNameAndValueList.size(); i++) {
            String clazzName = methodArgTypeList.get(i);
            JsonNode argValue = argNameAndValueList.get(i);
            Object arg = null;
            if (clazzName == null || clazzName == "null" || StringUtils.isBlank(clazzName)) {
                // do nothing
            }else {
                arg = JsonUtil.toBeanWithCanonical(argValue.toString(), clazzName);
            }
            args.add(arg);
        }
        return args.toArray();
    }

    public Object getBeanInstence(Class bizClazz){
        ApplicationContext applicationContext = getApplicationContext();
        Map<String, Object> bizBeans = applicationContext.getBeansOfType(bizClazz);
        if (bizBeans.size() == 2) {
            Iterator<String> keyIt = bizBeans.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                if (ScopedProxyUtils.isScopedTarget(key)) { // 针对refreshScope单独处理
                    keyIt.remove();
                }
            }
        }
        RedoCheckUtils.check(bizBeans != null && bizBeans.size() == 1);
        Object bizBean = bizBeans.values().iterator().next();
        return bizBean;
    }


    public static void main(String[] args) throws ClassNotFoundException {
        // test array
        String typeStr = "[Ljava.lang.String;";
        Pattern p = Pattern.compile("\\[L.+;");
        Matcher m = p.matcher(typeStr);
        Class clazz = null;
        String data = null;
        Object object = null;
        if (m.find()) {
            clazz = Class.forName(typeStr);
            data = "[\"1\",\"2\"]";
            object = JsonUtil.toArray(data, clazz);
            System.out.println(object);
        }
        // test complex array
        typeStr = "[Lcom.sunlands.insurance.redo.model.SysRedo;";
        clazz = Class.forName(typeStr);
        data = "[{\"id\":1},{\"id\":2}]";
        object = JsonUtil.toArray(data, clazz);
        System.out.println(object);

        // test collection
        typeStr = "java.util.ArrayList<java.lang.String>";
        p = Pattern.compile("(.+)<([^,]+)>");
        m = p.matcher(typeStr);
        String collectionTypeStr = null;
        String genericTypeStr = null;
        if (m.find()) {
            collectionTypeStr = m.group(1);
            genericTypeStr = m.group(2);
        }
        data = "[\"1\",\"2\"]";
        Class collectionClazz = Class.forName(collectionTypeStr);
        Class contentClazz = Class.forName(genericTypeStr);
        object = JsonUtil.toCollection(data, collectionClazz, contentClazz);
        System.out.println(object);

        // test map
        typeStr = "java.util.HashMap<java.lang.String,java.lang.Long>";
        p = Pattern.compile("(.+)<(.+),(.+)>");
        m = p.matcher(typeStr);
        String mapTypeStr = null;
        String keyTypeStr = null;
        String valueTypeStr = null;
        if (m.find()) {
            mapTypeStr = m.group(1);
            keyTypeStr = m.group(2);
            valueTypeStr = m.group(3);
        }
        data = "{\"a\": 1,\"b\": 2,\"c\": 3}";
        Class mapClazz = Class.forName(mapTypeStr);
        Class keyClazz = Class.forName(keyTypeStr);
        Class valueClazz = Class.forName(valueTypeStr);
        object = JsonUtil.toMap(data, mapClazz, keyClazz, valueClazz);
        System.out.println(object);

        // test jackson TypeParser
        object = JsonUtil.toBeanWithCanonical("[\"1\",\"2\"]", "[Ljava.lang.String;");
        Class[] type = JsonUtil.toRawType("[\"[Lcom.sunlands.insurance.redo.model.SysRedo;\"]");
        SysRedo[] redoList = new SysRedo[2];
        System.out.println(redoList.getClass());
        System.out.println(object);
    }
}
