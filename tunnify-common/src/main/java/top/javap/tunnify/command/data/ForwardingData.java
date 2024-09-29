package top.javap.tunnify.command.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ForwardingData {
    private String channelId;
    private byte[] data;
}