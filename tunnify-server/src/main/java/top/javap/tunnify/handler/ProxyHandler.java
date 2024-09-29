package top.javap.tunnify.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.CommandEnum;
import top.javap.tunnify.command.data.ConnectProxyData;
import top.javap.tunnify.command.data.ForwardingData;
import top.javap.tunnify.protocol.TunnifyMessage;

@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class ProxyHandler extends ChannelInboundHandlerAdapter {
    private final Channel target;
    private final String targetHost;
    private final int targetPort;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TunnifyMessage<ConnectProxyData> message = new TunnifyMessage<>(CommandEnum.PROXY_CONNECT.getCode(),
                new ConnectProxyData(targetHost, targetPort, ctx.channel().id().asLongText()));
        target.writeAndFlush(message);
        log.debug("proxy connection {} -> {}:{}", ctx.channel().remoteAddress(), targetHost, targetPort);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            TunnifyMessage<ForwardingData> message = new TunnifyMessage<>(CommandEnum.DATA_FORWARDING.getCode(),
                    new ForwardingData(ctx.channel().id().asLongText(), ByteBufUtil.getBytes((ByteBuf) msg)));
            target.writeAndFlush(message);
            log.debug("data forwarding {} -> {}:{}", ctx.channel().remoteAddress(), targetHost, targetPort);
        }
        super.channelRead(ctx, msg);
    }
}
