package top.javap.tunnify.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import top.javap.tunnify.command.Command;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyRawMessage;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public abstract class TunnifyCommandHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TunnifyRawMessage) {
            TunnifyRawMessage message = (TunnifyRawMessage) msg;
            process(ctx, message);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    protected abstract void process(ChannelHandlerContext ctx, TunnifyRawMessage message);
}