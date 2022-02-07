package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.TrpcChannel;
import io.netty.channel.Channel;


/**
 * @auther dewey
 * @date 2022/2/7 22:27
 */
public class NettyChannel implements TrpcChannel {
    private Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(byte[] message) {
        channel.writeAndFlush(message);
    }
}
