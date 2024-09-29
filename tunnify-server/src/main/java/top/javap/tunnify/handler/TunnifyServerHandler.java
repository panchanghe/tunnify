package top.javap.tunnify.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.CommandEnum;
import top.javap.tunnify.command.data.AuthenticationData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.command.data.OpenProxyData;
import top.javap.tunnify.exceptions.TunnifyException;
import top.javap.tunnify.protocol.TunnifyMessage;
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
@RequiredArgsConstructor
public class TunnifyServerHandler extends TunnifyCommandHandler {
    private static final Map<Integer, ProxyServer> proxyServers = new ConcurrentHashMap<>();
    private final String password;
    private boolean authenticated = false;

    @Override
    protected void process(ChannelHandlerContext ctx, TunnifyRawMessage message) {
        CommandEnum commandEnum = CommandEnum.getByCode(message.getCommand());
        if (allowAccess(ctx, commandEnum)) {
            switch (commandEnum) {
                case AUTHENTICATION -> processAuthentication(ctx, message.getDataObject(AuthenticationData.class));
                case OPEN_PROXY -> processOpenProxy(ctx, message.getDataObject(OpenProxyData.class));
                case DATA_FORWARDING -> processDataForwarding(ctx, message.getDataObject(ForwardingData.class));
                default -> throw new TunnifyException("Invalid command:" + message.getCommand());
            }
        }
    }

    private boolean allowAccess(ChannelHandlerContext ctx, CommandEnum commandEnum) {
        if (authenticated || CommandEnum.AUTHENTICATION.equals(commandEnum)) {
            return true;
        }
        TunnifyMessage message = new TunnifyMessage(CommandEnum.ACCESS_DENIED.getCode(), new MessageData("Access denied, please authenticate first"));
        ctx.writeAndFlush(message);
        return false;
    }

    private void processDataForwarding(ChannelHandlerContext ctx, ForwardingData forwardingData) {
        channels.writeAndFlush(Unpooled.copiedBuffer(forwardingData.getData()),
                channel -> {
                    return channel.id().asLongText().equals(forwardingData.getChannelId());
                });
    }

    @SneakyThrows
    private void processOpenProxy(ChannelHandlerContext ctx, OpenProxyData openProxyData) {
        Assert.isFalse(proxyServers.containsKey(openProxyData.getRemotePort()), "port is already in use");
        ProxyServer proxyServer = ProxyServer.bind(openProxyData.getRemotePort(), channels, new ProxyHandler(ctx.channel(), openProxyData.getLocalPort()));
        proxyServers.put(openProxyData.getRemotePort(), proxyServer);
    }

    private void processAuthentication(ChannelHandlerContext ctx, AuthenticationData authenticationData) {
        if (Objects.equals(this.password, authenticationData.getPassword())) {
            authenticated = true;
            log.info("Connection successful,remote:{}", ctx.channel().remoteAddress());
        } else {
            TunnifyMessage message = new TunnifyMessage(CommandEnum.ACCESS_DENIED.getCode(), new MessageData("Password error"));
            ctx.writeAndFlush(message);
        }
    }
}