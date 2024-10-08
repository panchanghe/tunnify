package top.javap.tunnify.command.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectProxyData {
    private String targetHost;
    private int targetPort;
    private String channelId;
}