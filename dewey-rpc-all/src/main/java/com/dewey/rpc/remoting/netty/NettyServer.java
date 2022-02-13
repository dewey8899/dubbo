package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.Codec;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/6 14:16
 */
@Slf4j
public class NettyServer implements Server {
    //开启一个网络服务
    //创建事件循环组
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();
    public void start(URI uri, Codec codec, Handler handler) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    //指定所使用的nio传输channel
                    .channel(NioServerSocketChannel.class)
                    //指定要监听的地址
            .localAddress(new InetSocketAddress(uri.getHost(),uri.getPort()))
                    //添加handler--有连接之后的处理逻辑
            .childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) {
                    //协议编解码【RpcInvocation】
                    ch.pipeline().addLast(new NettyCodec(codec.createInstance()));
                    //具体的逻辑执行
                    ch.pipeline().addLast(new NettyHandler(handler));
                }
            });
            ChannelFuture future = bootstrap.bind().sync();
            log.info("完成端口绑定和服务器启动");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
