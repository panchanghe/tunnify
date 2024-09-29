package top.javap.tunnify.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.CommandEnum;
import top.javap.tunnify.command.data.AuthenticationData;
import top.javap.tunnify.command.data.ConnectProxyData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.exceptions.TunnifyException;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyRawMessage;
import top.javap.tunnify.proxy.ProxyConnection;
import top.javap.tunnify.utils.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
public class TunnifyClientHandler extends TunnifyCommandHandler {
    private static final Map<String, ProxyConnection> proxyConnections = new ConcurrentHashMap<>();
    private final String password;

    public TunnifyClientHandler(String password) {
        Assert.hasText(password, "password cannot be empty");
        this.password = password;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TunnifyMessage<AuthenticationData> message = new TunnifyMessage(CommandEnum.AUTHENTICATION.getCode(), new AuthenticationData(password));
        ctx.writeAndFlush(message);
        super.channelInactive(ctx);
    }

    @Override
    protected void process(ChannelHandlerContext ctx, TunnifyRawMessage message) {
        CommandEnum commandEnum = CommandEnum.getByCode(message.getCommand());
        switch (commandEnum) {
            case ACCESS_DENIED -> processAccessDenied(ctx, message.getDataObject(MessageData.class));
            case DISCONNECT -> processDisconnect(ctx, message.getDataObject(MessageData.class));
            case PROXY_CONNECT -> processProxyConnect(ctx, message.getDataObject(ConnectProxyData.class));
            case DATA_FORWARDING -> processForwarding(ctx, message.getDataObject(ForwardingData.class));
            default -> throw new TunnifyException("Invalid command:" + message.getCommand());
        }
    }

    private void processAccessDenied(ChannelHandlerContext ctx, MessageData messageData) {
        log.info("The server denies access,because:{}", messageData.getMessage());
        ctx.close();
    }

    private void processForwarding(ChannelHandlerContext ctx, ForwardingData forwardingData) {
        ProxyConnection proxyConnection = proxyConnections.get(forwardingData.getChannelId());
        if (proxyConnection != null) {
            proxyConnection.send(forwardingData.getData());
        }
    }

    @SneakyThrows
    private void processProxyConnect(ChannelHandlerContext ctx, ConnectProxyData connectProxyData) {
        final String channelId = connectProxyData.getChannelId();
        ProxyConnection proxyConnection = ProxyConnection.connect("127.0.0.1", connectProxyData.getLocalPort(), new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
            }

            @Override
            public void channelRead(ChannelHandlerContext ccc, Object msg) throws Exception {
                TunnifyMessage<ForwardingData> message = new TunnifyMessage<>(CommandEnum.DATA_FORWARDING.getCode(),
                        new ForwardingData(channelId, ByteBufUtil.getBytes((ByteBuf) msg)));
                ctx.writeAndFlush(message);
            }
        });
        proxyConnections.put(connectProxyData.getChannelId(), proxyConnection);
    }

    private void processDisconnect(ChannelHandlerContext ctx, MessageData messageData) {
        log.error("Loss of connection,because:{}", messageData.getMessage());
        ctx.close();
    }
}