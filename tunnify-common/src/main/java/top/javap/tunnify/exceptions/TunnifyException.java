package top.javap.tunnify.exceptions;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
public class TunnifyException extends RuntimeException {

    public TunnifyException(String message) {
        super(message);
    }

    public TunnifyException(String message, Throwable cause) {
        super(message, cause);
    }
}