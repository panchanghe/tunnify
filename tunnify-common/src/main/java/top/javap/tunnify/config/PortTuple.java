package top.javap.tunnify.config;

import lombok.Data;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public class PortTuple {
    private Integer localPort;
    private Integer remotePort;
}