package top.javap.tunnify.config;

import lombok.Data;

import java.util.List;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public class ClientConfiguration extends BaseConfiguration {
    private List<PortTuple> portMapping;
}