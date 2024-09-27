package top.javap.tunnify.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.data.ConnectData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.command.data.OpenProxyData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;
import top.javap.tunnify.protocol.TunnifyRawMessage;
import top.javap.tunnify.proxy.ProxyManager;

import java.util.Objects;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
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
//        ProxyManager.openProxy();
        System.err.println("openProxy:" + ctx.channel().remoteAddress());
    }

    private void processConnect(ChannelHandlerContext ctx, ConnectData connectData) {
        if (true) {
            log.info("Connection successful,remote:{}", ctx.channel().remoteAddress());
        } else {
            ctx.writeAndFlush(new TunnifyMessage<>(TunnifyMessageConstant.COMMAND_CLOSE, new MessageData("Password error")));
            ctx.close();
        }
    }
}