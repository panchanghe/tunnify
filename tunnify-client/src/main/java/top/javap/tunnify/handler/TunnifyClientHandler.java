package top.javap.tunnify.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.Command;
import top.javap.tunnify.command.CommandConstant;
import top.javap.tunnify.command.data.ConnectData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;
import top.javap.tunnify.protocol.TunnifyRawMessage;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@AllArgsConstructor
@Slf4j
public class TunnifyClientHandler extends TunnifyCommandHandler {
    private final String password;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TunnifyMessage<ConnectData> message = new TunnifyMessage(TunnifyMessageConstant.COMMAND_CONNECT, new ConnectData(password));
        ctx.writeAndFlush(message);
        super.channelInactive(ctx);
    }

    @Override
    protected void process(ChannelHandlerContext ctx, TunnifyRawMessage message) {
        if (TunnifyMessageConstant.COMMAND_CLOSE.equals(message.getCommand())) {
            processClose(ctx, message.getDataObject(MessageData.class));
        }
    }

    private void processClose(ChannelHandlerContext ctx, MessageData messageData) {
        log.error("Loss of connection,because:{}", messageData.getMessage());
        ctx.close();
    }
}