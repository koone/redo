package com.lk.redo.util;

import java.util.Arrays;
import java.util.List;

/**
 * RedoConstants
 * 
 */
public class RedoConstants {

    // status
    public static final Byte STATUS_NEW = new Byte("0");
    public static final Byte STATUS_HANDLE_SUCCESS = 1;
    public static final Byte STATUS_HANDLE_FAILED = 2;
    public static final Byte STATUS_NOT_NEED_HANDLE = 3;

    public static final int MAX_LENGTH_BIZ_INVOKE_ERROR_STACK = 1980;

    public static final String STAT_NEW_ADD_COUNT_FIELD_NAME = "newAddCount";
    public static final long STAT_NEW_ADD_COUNT_DEFAULT_TIME_RANGE = 5; // 5分钟
    public static final String STAT_OVERSTOCKED_COUNT_FIELD_NAME = "overstockedCount";
    public static final long STAT_OVERSTOCKED_DEFAULT_TIME_RANGE = 24; // 24小时
    public static final String STAT_ALL_COUNT_FIELD_NAME = "allCount";
}
