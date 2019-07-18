package com.lk.redo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 直接将该注解添加到，发生异常后需要事后重做的业务方法上 <br>
 * 注意几点：<br>
 * 1) 该注解不会自动重做，事后需要人工干预重做<br>
 * 2) 当且仅当添加注解的业务方法抛出任何Throwable时候，才会判定为发生业务异常，记录重走 <br>
 * 3) 业务方自行保证RedoHandler在执行重做时候的环境要求，例如：事故现场和RedoHandler执行时 Session和ThreadLocal的差异 <br>
 * 4) 业务方自行保证RedoHandler在执行重做时候的业务要求，例如：业务幂等性等方面的要求 <br>
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Redo {
    /**
     * redo事件的类型标识符，业务方自行规划取名，无特殊要求<br>
     * 建议取一个能很好代表该事件业务含义的标识符
     *
     * @return
     */
    String type();

    /**
     * 是否抛出异常
     * @return
     */
    boolean throwAble() default false;

    /**
     * 能否通过任务自动重做，默认false <br>
     * @return
     */
    boolean autoRedoAble() default false;

    /**
     * 自动重试次数限制，默认3
     * @return
     */
    int autoRedoLimit() default 3;

    /**
     * redo事件重做时候的处理器的标识符id，处理器必须实现RedoHandler接口。目前依赖于spring的实现，该标识符即为spring bean id
     * @return spring bean id
     */
    String handlerName() default "defaultRedoHandler";

    /**
     * 同时定义redoFor和noRedoFor时，只考虑redoFor<br>
     * 定义0个或更多个Throwable类的Class对象(只考虑类型的直接匹配，不考虑父子类匹配)，代表这些异常会将触发redo<br>
     * 如果未定义则代表所有Throwable类都会触发redo
     */
    Class<? extends Throwable>[] redoFor() default {};

    /**
     * 同时定义redoFor和noRedoFor时，只考虑redoFor<br>
     * 定义0个或更多个Throwable类的Class对象(只考虑类型的直接匹配，不考虑父子类匹配)，代表这些异常会将不会触发redo<br>
     * 如果未定义则代表所有Throwable类都会触发redo
     */
    Class<? extends Throwable>[] noRedoFor() default {};
}