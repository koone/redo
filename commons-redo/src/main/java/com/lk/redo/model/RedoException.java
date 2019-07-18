package com.lk.redo.model;

/**
 * UredoException
 * 
 */
public class RedoException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5176582834954755702L;

    /**
     * 构造方法
     */
    public RedoException() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param message 异常信息
     */
    public RedoException(String message) {
        super(message);
    }

    /**
     * 构造方法
     * 
     * @param message 异常信息
     * @param cause 异常类
     */
    public RedoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造方法
     * 
     * @param cause 异常类
     */
    public RedoException(Throwable cause) {
        super(cause);
    }
}
