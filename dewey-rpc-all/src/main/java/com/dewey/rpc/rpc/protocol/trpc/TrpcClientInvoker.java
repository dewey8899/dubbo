package com.dewey.rpc.rpc.protocol.trpc;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.remoting.Client;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.Response;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.trpc.handler.TrpcClientHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @auther dewey
 * @date 2022/2/19 16:25
 */
public class TrpcClientInvoker implements Invoker {
    private Client client;
    private Serialization serialization;

    public TrpcClientInvoker(Client client, Serialization serialization) {
        this.client = client;
        this.serialization = serialization;
    }

    @Override
    public Class getInterface() {
        return null;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        //1、序列化 rpcInvocation--根据服务提供者的配置决定
        byte[] requestBody = serialization.serialize(rpcInvocation);
        //2、发起请求 向远处发起访问--
        this.client.getChannel().send(requestBody);
        //3、接受响应   解码后 handler
        //实现等待结果的机制
        CompletableFuture completableFuture = TrpcClientHandler.waitResult(rpcInvocation.getId());
        //future get 获取结果
        Object result = completableFuture.get(60, TimeUnit.SECONDS);
        Response response = (Response) result;
        if (response.getStatus() == 200) {
            return response.getContent();
        }else {
            throw new Exception("server error:" + response.getContent().toString());
        }
    }
}
