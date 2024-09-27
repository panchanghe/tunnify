package top.javap.tunnify.protocol;

import com.alibaba.fastjson2.JSONB;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyRawMessage extends TunnifyMessage<byte[]> {

    public TunnifyRawMessage(Integer command, byte[] data) {
        super(command, data);
    }

    public <T> T getDataObject(Class<T> clazz) {
        return JSONB.parseObject(getData(), clazz);
    }
}