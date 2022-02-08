package com.dewey.rpc.remoting;

/**
 * @auther dewey
 * @date 2022/2/7 22:11
 * 由具体的协议去实现
 */
public interface Handler {

    //message 就是rpcinvocation
    void onReceive(TrpcChannel trpcChannel,Object message) throws Exception;

    void onWrite(TrpcChannel trpcChannel,Object message) throws Exception;
}
