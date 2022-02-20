package com.dewey.rpc.remoting;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/19 16:36
 */
public interface Client {
    void connect(URI uri, Codec codec, Handler handler) throws InterruptedException;

    TrpcChannel getChannel();
}
