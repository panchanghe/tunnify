package top.javap.tunnify.config;

import lombok.Data;

@Data
public class PortMapping {
    private Integer serverPort;
    private String targetHost;
    private Integer targetPort;
}
