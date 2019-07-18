package com.lk.redo.commons.util.utils;

import com.lk.redo.commons.util.exception.BusinessException;
import com.lk.redo.commons.util.enums.VoCodeEnum;

/**
 * @Author chenao
 * @Description
 * @Date Created in 2018/10/22.
 */
public class ExceptionUtil {

    /**
     * 非正常业务处理
     *
     * @param codeEnum
     */
    public static void processAbnormal(VoCodeEnum codeEnum) {
        throw new BusinessException(codeEnum.getCode(), codeEnum.getMsg());
    }
}
