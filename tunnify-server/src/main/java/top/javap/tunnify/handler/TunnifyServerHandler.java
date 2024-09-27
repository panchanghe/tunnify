package top.javap.tunnify.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import top.javap.tunnify.command.data.ConnectData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.command.data.OpenProxyData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;
import top.javap.tunnify.protocol.TunnifyRawMessage;

import java.util.Objects;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyServerHandler extends TunnifyCommandHandler {

    @Override
    protected void process(ChannelHandlerContext ctx, TunnifyRawMessage message) {
        if (Objects.equals(TunnifyMessageConstant.COMMAND_CONNECT, message.getCommand())) {
            processConnect(ctx, message.getDataObject(ConnectData.class));
        } else if (Objects.equals(TunnifyMessageConstant.COMMAND_OPEN_PROXY, message.getCommand())) {
            processOpenProxy(ctx, message.getDataObject(OpenProxyData.class));
        }
    }

    private void processOpenProxy(ChannelHandlerContext ctx, OpenProxyData openProxyData) {
        int remotePort = openProxyData.getRemotePort();
        
    }

    private void processConnect(ChannelHandlerContext ctx, ConnectData connectData) {
        if (true) {

        } else {
            ctx.writeAndFlush(new TunnifyMessage<>(TunnifyMessageConstant.COMMAND_CLOSE, new MessageData("Password error")));
            ctx.close();
        }
    }
}