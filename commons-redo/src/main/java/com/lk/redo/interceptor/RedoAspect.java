package com.lk.redo.interceptor;

import com.lk.redo.annotation.Redo;
import com.lk.redo.service.RedoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Redo切面，拦截@Redo注解方法<br>
 * RedoAspect捕获到触发redo的异常后，将会妥善处理异常，保存异常现场，并终止继续上抛，从而让原本的transactionInterceptor、logInterceptor等可以继续正常工作
 * 
 */
@Slf4j
@Aspect
@Component
@Order(20)
public class RedoAspect {

    @Resource
    private RedoService redoService;

    @Pointcut("@annotation(redo)")
    public void methodPointcut(Redo redo) {}

    @Around("methodPointcut(redo)")
    public Object invoke(ProceedingJoinPoint jp, Redo redo) throws Throwable {
        // 执行业务逻辑
        Object result = null;
        try {
            result = jp.proceed();
        } catch (Throwable t) {
            boolean checkRedoForException = checkUredoForException(redo, t);
            if (checkRedoForException){
                // 保存redo执行现场
                try {
                    MethodSignature methodSignature = (MethodSignature) jp.getSignature();
                    log.error("failed to invoke business method and record RedoItem type={}", redo.type(), t );
                    redoService.addRedoItem(redo, t, jp.getTarget().getClass(), methodSignature, jp.getArgs());
                } catch (Exception e) {
                    log.error("failed to add redoItem type={}", redo.type(), e);
                }
                if (redo.throwAble()) {
                    throw t;
                }
            }else {
                // 不重做，直接返回异常
                throw t;
            }
        }
        return result;
    }

    /**
     * 检查当前的业务异常是否被redo的redoFor和noRedoFor属性命中<br>
     * 只考虑类型的直接匹配，不考虑父子类匹配<br>
     * 注意同时定义redoFor和noRedoFor时，只考虑redoFor
     * @param redo redo注解声明
     * @param bizException 业务异常
     * @return 业务异常类型是否被redo的redoFor和noRedoFor属性命中
     */
    private boolean checkUredoForException(Redo redo, Throwable bizException) {
        if (bizException == null || redo == null) {
            return false;
        }
        if (RedoService.isRedoWorkModel()) {
            return false;
        }
        Class<? extends Throwable>[] redoForClazzs = redo.redoFor();
        Class<? extends Throwable>[] noRedoForClazzs = redo.noRedoFor();
        if ((redoForClazzs == null || redoForClazzs.length == 0)
                && (noRedoForClazzs == null || noRedoForClazzs.length == 0)) {
            return true;
        } else if (redoForClazzs != null && redoForClazzs.length > 0) {
            for (Class<? extends Throwable> redoForClazz : redoForClazzs) {
                if (redoForClazz.equals(bizException.getClass())) {
                    return true;
                }
            }
            return false;
        } else if (noRedoForClazzs != null && noRedoForClazzs.length > 0) {
            for (Class<? extends Throwable> noRedoForClazz : noRedoForClazzs) {
                if (noRedoForClazz.equals(bizException.getClass())) {
                    return false;
                }
            }
            return true;
        }
        return true;

    }


}
