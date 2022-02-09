package com.dewey.rpc.rpc.protocol.trpc.handler;

import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.TrpcChannel;
import com.dewey.rpc.rpc.RpcInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther dewey
 * @date 2022/2/8 23:34
 */
@Slf4j
public class TrpcServerHandler implements Handler {
    @Override
    public void onReceive(TrpcChannel trpcChannel, Object message) throws Exception {
        RpcInvocation rpcInvocation = (RpcInvocation) message;
        log.info("收到rpcInvocation信息：{}",rpcInvocation);
        //TODO 发起调用

    }

    @Override
    public void onWrite(TrpcChannel trpcChannel, Object message) throws Exception {

    }
}
