package top.javap.tunnify.config;

import lombok.Data;
import top.javap.tunnify.utils.Assert;

import java.util.List;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public class ClientConfiguration extends BaseConfiguration {
    private String serverHost;
    private Integer serverPort;
    private List<PortMapping> portMappings;

    @Override
    public void check() {
        super.check();
        Assert.hasText(serverHost, "serverHost cannot be empty");
        Assert.notNull(serverPort, "serverPort cannot be empty");
        Assert.isNotEmpty(portMappings, "portMappings cannot be empty");
    }
}