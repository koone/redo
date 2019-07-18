package com.lk.redo.util;


import com.lk.redo.model.RedoException;

/**
 * RedoCheckUtils
 */
public class RedoCheckUtils {
    public static void check(boolean expression) {
        if (!expression) {
            throw new RedoException();
        }
    }

    public static void check(boolean expression, String errorMessage) {
        if (expression) {
            throw new RedoException(errorMessage);
        }
    }

    public static void checkNotNull(Object expression) {
        check(expression != null);
    }

    public static void checkNotNull(Object expression, String errorMessage) {
        check(expression != null, errorMessage);
    }

    public static void checkNotBlank(String expression) {
        boolean result = (expression != null && !expression.trim().isEmpty());
        check(result);
    }

    public static void checkNotBlank(String expression, String errorMessage) {
        boolean result = (expression != null && !expression.trim().isEmpty());
        check(result, errorMessage);
    }

}
