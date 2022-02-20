package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.*;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/6 14:13
 */
public class Netty4Transporter implements Transporter {
    public Server start(URI uri,Codec codec, Handler handler) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(uri, codec, handler);
        return nettyServer;
    }

    @Override
    public Client connect(URI uri, Codec codec, Handler handler) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(uri,codec,handler);
        return nettyClient;
    }
}
