package top.javap.tunnify.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;

@RequiredArgsConstructor
public class ProxyConnection {
    private final Channel channel;

    @SneakyThrows
    public static ProxyConnection connect(String host, int port, ChannelHandler channelHandler) {
        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        ChannelFuture channelFuture = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                sc.pipeline().addLast(channelHandler);
            }
        }).connect(host, port).sync();
        channelFuture.addListener(f -> group.shutdownGracefully());
        return new ProxyConnection(channelFuture.channel());
    }

    public void send(byte[] data) {
        channel.writeAndFlush(Unpooled.copiedBuffer(data));
    }
}
