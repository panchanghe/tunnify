package top.javap.tunnify.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.data.ConnectData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.command.data.OpenProxyData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;
import top.javap.tunnify.protocol.TunnifyRawMessage;
import top.javap.tunnify.proxy.ProxyServer;
import top.javap.tunnify.utils.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
public class TunnifyServerHandler extends TunnifyCommandHandler {
    private static final Map<Integer, ProxyServer> proxyServers = new ConcurrentHashMap<>();

    @Override
    protected void process(ChannelHandlerContext ctx, TunnifyRawMessage message) {
        if (Objects.equals(TunnifyMessageConstant.COMMAND_CONNECT, message.getCommand())) {
            processConnect(ctx, message.getDataObject(ConnectData.class));
        } else if (Objects.equals(TunnifyMessageConstant.COMMAND_OPEN_PROXY, message.getCommand())) {
            processOpenProxy(ctx, message.getDataObject(OpenProxyData.class));
        } else if (Objects.equals(TunnifyMessageConstant.COMMAND_FORWARDING, message.getCommand())) {
            ForwardingData forwardingData = message.getDataObject(ForwardingData.class);
            Channel channel = channels.get(forwardingData.getChannelId());
            if (channel != null) {
                channel.writeAndFlush(Unpooled.copiedBuffer(forwardingData.getData()));
            }
        }
    }

    @SneakyThrows
    private void processOpenProxy(ChannelHandlerContext ctx, OpenProxyData openProxyData) {
        Assert.isFalse(proxyServers.containsKey(openProxyData.getRemotePort()), "port is already in use");
        ProxyServer proxyServer = ProxyServer.bind(openProxyData.getRemotePort(), channels, new ProxyHandler(ctx.channel(), openProxyData.getLocalPort()));
        proxyServers.put(openProxyData.getRemotePort(), proxyServer);
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