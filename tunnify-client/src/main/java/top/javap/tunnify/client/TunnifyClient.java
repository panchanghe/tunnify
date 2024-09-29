package top.javap.tunnify.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.CommandEnum;
import top.javap.tunnify.command.data.OpenProxyData;
import top.javap.tunnify.handler.TunnifyClientHandler;
import top.javap.tunnify.handler.TunnifyMessageCodec;
import top.javap.tunnify.protocol.TunnifyMessage;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@RequiredArgsConstructor
@Slf4j
public class TunnifyClient {
    private final String serverHost;
    private final int serverPort;
    private final String password;
    private Channel channel;

    public ChannelFuture connect() throws Exception {
        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        ChannelFuture channelFuture = new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new TunnifyMessageCodec());
                        sc.pipeline().addLast(new TunnifyClientHandler(password));
                    }
                }).connect(serverHost, serverPort).sync();
        this.channel = channelFuture.channel();
        this.channel.closeFuture().addListener(f -> group.shutdownGracefully());
        return channelFuture;
    }

    public void openProxy(int serverPort, String targetHost, int targetPort) {
        TunnifyMessage<OpenProxyData> message = new TunnifyMessage<>(CommandEnum.OPEN_PROXY.getCode(), new OpenProxyData(serverPort, targetHost, targetPort));
        channel.writeAndFlush(message);
        log.info("open proxy {} -> {}:{}", serverPort, targetHost, targetPort);
    }
}