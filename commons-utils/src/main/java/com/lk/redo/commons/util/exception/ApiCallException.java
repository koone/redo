package com.lk.redo.commons.util.exception;

/**
 * @Description:  调用三方服务异常
 * @Author: luokun
 * @Date: 2018/9/20 下午12:54
 */
public class ApiCallException extends RuntimeException{

    public ApiCallException() {
        super();
    }

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable t) {
        super(message, t);
    }

}
