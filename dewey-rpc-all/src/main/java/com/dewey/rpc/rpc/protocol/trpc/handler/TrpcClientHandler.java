package com.dewey.rpc.rpc.protocol.trpc.handler;

import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.TrpcChannel;
import com.dewey.rpc.rpc.Response;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther dewey
 * @date 2022/2/19 17:13
 */
public class TrpcClientHandler implements Handler {
    //多少个线程来调佣 远程服务
    private static final Map<Long, CompletableFuture> invokerMap = new ConcurrentHashMap<>();
    //登记一下，创建返回一个future ，每一个等待结果的线程一个单独的future
    public static CompletableFuture waitResult(long messageId){
        CompletableFuture future = new CompletableFuture();
        invokerMap.put(messageId, future);
        return future;
    }
    /**
     * 客户端而言，收到响应 --- 方法执行的返回值
     * @param trpcChannel
     * @param message
     * @throws Exception
     * 这个方法 是 netty eventGroup
     */
    @Override
    public void onReceive(TrpcChannel trpcChannel, Object message) throws Exception {
        Response response = (Response) message;
        //接受所有的结果  -- 和invoker 调用者不在一个线程
        //根据id 和 具体的请求对应起来 complete发送结果
        invokerMap.get(response.getRequestId()).complete(response);
        invokerMap.remove(response.getRequestId());
    }

    @Override
    public void onWrite(TrpcChannel trpcChannel, Object message) throws Exception {

    }
}
