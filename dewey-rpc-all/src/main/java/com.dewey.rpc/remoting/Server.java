package com.dewey.rpc.remoting;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/6 14:10
 * 启动一个网络访问服务
 */
public interface Server {
    void start(URI uri);
}
