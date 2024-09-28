package top.javap.tunnify.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.Command;
import top.javap.tunnify.command.CommandConstant;
import top.javap.tunnify.command.data.ConnectData;
import top.javap.tunnify.command.data.ConnectProxyData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.command.data.MessageData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;
import top.javap.tunnify.protocol.TunnifyRawMessage;
import top.javap.tunnify.proxy.ProxyConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@RequiredArgsConstructor
@Slf4j
public class TunnifyClientHandler extends TunnifyCommandHandler {
    private static final Map<String, ProxyConnection> proxyConnections = new ConcurrentHashMap<>();
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
        } else if (TunnifyMessageConstant.COMMAND_CONNECT_PROXY.equals(message.getCommand())) {
            processConnectProxy(ctx, message.getDataObject(ConnectProxyData.class));
        } else if (TunnifyMessageConstant.COMMAND_FORWARDING.equals(message.getCommand())) {
            processForwarding(ctx, message.getDataObject(ForwardingData.class));
        }
    }

    private void processForwarding(ChannelHandlerContext ctx, ForwardingData forwardingData) {
        ProxyConnection proxyConnection = proxyConnections.get(forwardingData.getChannelId());
        if (proxyConnection != null) {
            proxyConnection.send(forwardingData.getData());
            System.err.println("local send:" + forwardingData.getData().length);
        }
    }

    @SneakyThrows
    private void processConnectProxy(ChannelHandlerContext ctx, ConnectProxyData connectProxyData) {
        final String channelId = connectProxyData.getChannelId();
        System.err.println(channelId);
        ProxyConnection proxyConnection = ProxyConnection.connect("127.0.0.1", connectProxyData.getLocalPort(), new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ccc, Object msg) throws Exception {
                TunnifyMessage<ForwardingData> message = new TunnifyMessage<>(TunnifyMessageConstant.COMMAND_FORWARDING,
                        new ForwardingData(channelId, ByteBufUtil.getBytes((ByteBuf) msg)));
                ctx.writeAndFlush(message);
            }
        });
        proxyConnections.put(connectProxyData.getChannelId(), proxyConnection);
    }

    private void processClose(ChannelHandlerContext ctx, MessageData messageData) {
        log.error("Loss of connection,because:{}", messageData.getMessage());
        ctx.close();
    }
}