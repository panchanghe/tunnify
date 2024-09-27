package top.javap.tunnify;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.handler.TunnifyMessageCodec;
import top.javap.tunnify.handler.TunnifyServerHandler;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@AllArgsConstructor
@Slf4j
public class TunnifyServer {
    private final int port;

    public void start() throws Exception {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new TunnifyMessageCodec());
                        sc.pipeline().addLast(new TunnifyServerHandler());
                    }
                }).bind(port).sync();
        log.info("TunnifyServer Started:" + port);
    }
}