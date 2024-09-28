package top.javap.tunnify.protocol;

/**
 * @Author: pch
 * @Date: 2024/9/27 16:18
 * @Description:
 */
public interface TunnifyMessageConstant {
    Integer COMMAND_CLOSE = 99;
    Integer COMMAND_CONNECT = 100;
    Integer COMMAND_OPEN_PROXY = 101;
    Integer COMMAND_CONNECT_PROXY = 102;
    Integer COMMAND_FORWARDING = 103;
}