package top.javap.tunnify;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import top.javap.tunnify.client.TunnifyClient;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyClientStarter {
    public static void main(String[] args) throws Exception {
        TunnifyClient client = new TunnifyClient("127.0.0.1", 9000, "password");
        client.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                client.openProxy(80, 9001);
            }
        });
    }
}