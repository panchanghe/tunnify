package top.javap.tunnify.handler;

import com.alibaba.fastjson2.JSONB;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyRawMessage;

import java.util.List;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyMessageCodec extends ByteToMessageCodec<TunnifyMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TunnifyMessage tunnifyMessage, ByteBuf byteBuf) throws Exception {
        byte[] bytes = JSONB.toBytes(tunnifyMessage.getData());
        byteBuf.writeInt(4 + bytes.length);
        byteBuf.writeInt(tunnifyMessage.getCommand());
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> list) throws Exception {
        while (input.readableBytes() >= 4) {
            input.markReaderIndex();
            int length = input.readInt();
            if (input.readableBytes() >= length) {
                int command = input.readInt();
                byte[] bytes = new byte[length - 4];
                input.readBytes(bytes);
                list.add(new TunnifyRawMessage(command, bytes));
            } else {
                input.resetReaderIndex();
                return;
            }
        }
    }
}