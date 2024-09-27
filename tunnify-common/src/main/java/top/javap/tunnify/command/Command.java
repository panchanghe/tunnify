package top.javap.tunnify.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@AllArgsConstructor
@Getter
public class Command<T> {
    private final Integer code;
    private final T data;
}