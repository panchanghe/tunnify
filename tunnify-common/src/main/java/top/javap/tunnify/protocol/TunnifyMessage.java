package top.javap.tunnify.protocol;

import com.alibaba.fastjson2.JSONB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.javap.tunnify.command.Command;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TunnifyMessage<T> {
    private Integer command;
    private T data;
}