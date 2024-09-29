package top.javap.tunnify.config;

import lombok.Data;
import top.javap.tunnify.utils.Assert;

import java.util.Set;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public abstract class BaseConfiguration {
    private String mode;
    private String password;

    public void check() {
        Assert.inElements(mode, Set.of(ConfigValueConstant.MODE_SERVER, ConfigValueConstant.MODE_CLIENT), "mode must be SERVER or CLIENT");
        Assert.hasText(password, "For security, a password must be set");
    }

    public boolean isServer() {
        return ConfigValueConstant.MODE_SERVER.equalsIgnoreCase(mode);
    }
}