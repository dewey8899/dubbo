package com.dewey.rpc.remoting;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/6 14:09
 * 底层网络传输--统一入口,不管是服务或者客户端
 */
public interface Transporter {
    /**
     * 如：dubbo://127.0.0.1:8080
     * @param uri 服务器 ip,端口
     * @return
     */
    Server start(URI uri,Codec codec, Handler handler);

    //connection
    Client connect(URI uri, Codec codec, Handler handler) throws InterruptedException;
}
