package top.javap.tunnify.config;

import lombok.Data;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
@Data
public abstract class BaseConfiguration {
    private String mode;
    private String password;
}