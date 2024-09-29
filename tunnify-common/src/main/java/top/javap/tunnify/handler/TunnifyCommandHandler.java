package top.javap.tunnify.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.protocol.TunnifyRawMessage;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
public abstract class TunnifyCommandHandler extends ChannelInboundHandlerAdapter {
    protected static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TunnifyRawMessage) {
            TunnifyRawMessage message = (TunnifyRawMessage) msg;
            try {
                process(ctx, message);
            } catch (Exception e) {
                log.error("Command processing failed", e);
                ctx.close();
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    protected abstract void process(ChannelHandlerContext ctx, TunnifyRawMessage message);
}