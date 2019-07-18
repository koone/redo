package com.lk.redo.commons.util.vo;

import com.lk.redo.commons.util.enums.VoCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 *  封装统一的响应数据VO
 *
 * @author wuguangkuo
 */
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 6965671443768324878L;

    public static final String CODE_FAILURE = VoCodeEnum.FAIL.getCode();

    public static final String CODE_SUCCESS = VoCodeEnum.SUCCESS.getCode();

    private String code;
    private String msg;
    private T data;

    private static final ResultVO COMMON_SUCCESS_RESULT = new ResultVO<>(VoCodeEnum.SUCCESS, null);
    private static final ResultVO COMMON_FAIL_RESULT = new ResultVO<>(VoCodeEnum.FAIL, null);

    public ResultVO() {
        this(VoCodeEnum.SUCCESS, null);
    }

    public ResultVO(VoCodeEnum resultStatus, T data) {
        this.code = resultStatus.getCode();
        this.msg = resultStatus.getMsg();
        this.data = data;
    }

    public ResultVO(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultVO buildResult(VoCodeEnum resultStatus) {
        return new ResultVO<>(resultStatus, null);
    }

    public static <T> ResultVO buildResult(VoCodeEnum resultStatus, T data) {
        return new ResultVO<>(resultStatus, data);
    }

    public static ResultVO buildFailResult() {
        return COMMON_FAIL_RESULT;
    }

    public static ResultVO buildFailResult(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(VoCodeEnum.FAIL.getCode());
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static ResultVO buildSuccessResult() {
        return COMMON_SUCCESS_RESULT;
    }

    public static <T> ResultVO<T> buildSuccessResult(T data) {
        return new ResultVO<>(VoCodeEnum.SUCCESS, data);
    }


}
