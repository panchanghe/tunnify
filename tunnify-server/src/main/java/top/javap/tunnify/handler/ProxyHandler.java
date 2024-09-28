package top.javap.tunnify.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import top.javap.tunnify.command.data.ConnectProxyData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyMessageConstant;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ProxyHandler extends ChannelInboundHandlerAdapter {
    private final Channel target;
    private final int targetPort;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("proxy active:" + ctx.channel().remoteAddress());
        TunnifyMessage<ConnectProxyData> message = new TunnifyMessage<>(TunnifyMessageConstant.COMMAND_CONNECT_PROXY,
                new ConnectProxyData(targetPort, ctx.channel().id().asLongText()));
        target.writeAndFlush(message);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            TunnifyMessage<ForwardingData> message = new TunnifyMessage<>(TunnifyMessageConstant.COMMAND_FORWARDING,
                    new ForwardingData(ctx.channel().id().asLongText(), ByteBufUtil.getBytes((ByteBuf) msg)));
            target.writeAndFlush(message);
        }
        super.channelRead(ctx, msg);
    }
}
