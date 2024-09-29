package top.javap.tunnify.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Author: pch
 * @Date: 2024/9/29 15:44
 * @Description:
 */
@RequiredArgsConstructor
@Getter
public enum CommandEnum {
    DISCONNECT(99),
    AUTHENTICATION(100),
    ACCESS_DENIED(101),
    OPEN_PROXY(200),
    PROXY_CONNECT(102),
    DATA_FORWARDING(103),
    ;

    private final Integer code;

    public static CommandEnum getByCode(Integer code) {
        for (CommandEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}