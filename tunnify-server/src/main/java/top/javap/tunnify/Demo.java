package top.javap.tunnify;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
public class Demo {
    public static void main(String[] args) throws Exception {
//        startServer();
//        startClient();
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Channel channel = new ServerBootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                }).bind(8888).sync().channel();
        System.err.println("xx");
        channel.closeFuture().addListener(f -> {
            group.shutdownGracefully();
        });
        Thread.sleep(3000);
        channel.close();
    }

    private static void startClient() throws Exception {
        Channel channel = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new MyCodec());
                    }
                }).connect("127.0.0.1", 9000).sync().channel();
        while (true) {
            Thread.sleep(1000);
            channel.writeAndFlush(JSONObject.of("a", "1"));
        }
    }

    private static void startServer() throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new MyCodec());
                    }
                }).bind(9000).sync().await();
    }

    public static class MyCodec extends ByteToMessageCodec<JSONObject> {

        @Override
        protected void encode(ChannelHandlerContext ctx, JSONObject jsonObject, ByteBuf byteBuf) throws Exception {
            System.err.println("send:" + jsonObject.toJSONString());
            byte[] bytes = jsonObject.toJSONBBytes();
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> list) throws Exception {
            if (input.readableBytes() >= 4) {
                int length = input.getInt(0);
                if (input.readableBytes() >= length + 4) {
                    input.readInt();
                    byte[] bytes = new byte[length];
                    input.readBytes(bytes);
                    String json = new String(bytes);
                    System.err.println("receive:" + json);
                }
            }
        }
    }
}