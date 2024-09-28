package top.javap.tunnify.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.javap.tunnify.command.Command;
import top.javap.tunnify.protocol.TunnifyMessage;
import top.javap.tunnify.protocol.TunnifyRawMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
@Slf4j
public abstract class TunnifyCommandHandler extends ChannelInboundHandlerAdapter {
    protected static final Map<String, Channel> channels = new ConcurrentHashMap<>();

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