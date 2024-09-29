package top.javap.tunnify.config;

import lombok.Data;
import top.javap.tunnify.utils.Assert;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public class ServerConfiguration extends BaseConfiguration {
    private Integer port;

    @Override
    public void check() {
        super.check();
        Assert.notNull(port, "server port cannot be empty");
    }
}