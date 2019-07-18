package com.lk.redo.service;

import com.lk.redo.commons.util.utils.JsonUtil;
import com.lk.redo.model.RedoException;
import com.lk.redo.model.SysRedo;
import com.lk.redo.util.RedoCheckUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
        List<Class> argClazzs = JsonUtil.toList(methodArgTypeStr, Class.class);
        Class[] methodArgTypes = new Class[argClazzs.size()];
        argClazzs.toArray(methodArgTypes);
        // get method
        Method m = null;
        try {
            m = bizClazz.getDeclaredMethod(redoItem.getBizInvokeMethod(), methodArgTypes);
        } catch (Exception e) {
            throw new RedoException("failed to get biz method for " + redoItem, e);
        }
        // invoke
        Object[] args = deserialize(methodArgTypes,redoItem.getBizInvokeArgs());
        try {
            m.invoke(bizBean, args);
        } catch (Exception e) {
            throw new RedoException("failed to invoke biz method for " + redoItem, e);
        }
    }

    /**
     * 将getBizInvokeArgsStr()序列化得到的json array string，反序列化
     *
     *
     * @param methodArgTypes
     * @param bizInvokeArgsStr 序列化的json array string
     * @return argValue pair
     */
    public static Object[] deserialize(Class[] methodArgTypes, String bizInvokeArgsStr) {
        if (bizInvokeArgsStr == null || bizInvokeArgsStr.trim().isEmpty()) {
            return null;
        }
        List<String> argNameAndValueList = JsonUtil.toList(bizInvokeArgsStr, String.class);
        Iterator<String> iterator = argNameAndValueList.iterator();
        List<Object> args = new ArrayList<Object>();
        int index = 0;
        while (iterator.hasNext()) {
            String clazzName = iterator.next();
            String argValue = iterator.next();
            Class<?> clazz = null;
            try {
                if (clazzName == null || clazzName == "null") {
                    clazz = methodArgTypes[index];
                }else {
                    clazz = Class.forName(clazzName);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Object arg = null;
            if (clazz != null && argValue != null) {
                arg = JsonUtil.toBean(argValue, clazz);
            }
            args.add(arg);
            index++;
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
}
