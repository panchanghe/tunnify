package top.javap.tunnify.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.handler.ProxyHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyServer {
    private final Channel channel;

    public static ProxyServer bind(int port, Map<String, Channel> subChannels, ProxyHandler proxyHandler) {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Channel channel = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    subChannels.put(ctx.channel().id().asLongText(), ctx.channel());
                                    System.err.println(ctx.channel().id().asLongText());
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    subChannels.remove(ctx.channel().id().asLongText());
                                    super.channelInactive(ctx);
                                }
                            });
                            sc.pipeline().addLast(proxyHandler);
                        }
                    }).bind(port).sync().channel();
            channel.closeFuture().addListener(f -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
            log.info("open proxy {}", port);
            return new ProxyServer(channel);
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    public void close() {
        channel.close();
    }
}
