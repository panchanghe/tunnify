package top.javap.tunnify.utils;

import io.netty.util.internal.StringUtil;
import top.javap.tunnify.exceptions.TunnifyException;

public class Assert {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new TunnifyException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        isTrue(!expression, message);
    }

    public static void hasText(String text, String message) {
        isFalse(StringUtil.isNullOrEmpty(text), message);
    }
}
