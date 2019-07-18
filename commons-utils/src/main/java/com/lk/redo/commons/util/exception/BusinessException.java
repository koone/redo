package com.lk.redo.commons.util.exception;

import com.lk.redo.commons.util.enums.VoCodeEnum;

/**
 * @Description: 业务异常类
 * @Author: chenao
 * @Date: 2018/10/18 下午5:25
 */
public class BusinessException extends RuntimeException{

    private String errorCode;
    private String errorMsg;

    public BusinessException() {
        super();
    }

    public BusinessException(String errorCode, String errorMsg) {
        super(errorCode + "-" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(VoCodeEnum codeEnum) {
        super(codeEnum.getCode() + "-" + codeEnum.getMsg());
        this.errorCode = codeEnum.getCode();
        this.errorMsg = codeEnum.getMsg();
    }

    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
