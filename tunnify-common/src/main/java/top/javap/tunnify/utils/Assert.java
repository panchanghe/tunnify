package top.javap.tunnify.utils;

import io.netty.util.internal.StringUtil;
import top.javap.tunnify.config.PortMapping;
import top.javap.tunnify.exceptions.TunnifyException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public static void inElements(String e, Collection<String> elements, String message) {
        isTrue(elements.contains(e), message);
    }

    public static void notNull(Object value, String message) {
        isTrue(value != null, message);
    }

    public static void isNotEmpty(Collection collection, String message) {
        isTrue(collection != null && collection.size() > 0, message);
    }
}
