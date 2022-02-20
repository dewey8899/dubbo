package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.Client;
import com.dewey.rpc.remoting.Codec;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.TrpcChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/19 16:49
 */
public class NettyClient implements Client {
    TrpcChannel channel = null;
    EventLoopGroup group = null;

    @Override
    public void connect(URI uri, Codec codec, Handler handler) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyCodec(codec.createInstance()));
                        ch.pipeline().addLast(new NettyHandler(handler));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
        channel = new NettyChannel(channelFuture.channel());
        //优雅停机  -- kill pid --响应退出信号
        Thread thread = new Thread();
        thread.run();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("我要停机了");
                synchronized (NettyServer.class) {
                    group.shutdownGracefully().sync();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public TrpcChannel getChannel() {
        return channel;
    }
}
