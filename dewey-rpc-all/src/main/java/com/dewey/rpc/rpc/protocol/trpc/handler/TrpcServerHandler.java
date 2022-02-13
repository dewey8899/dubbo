package com.dewey.rpc.rpc.protocol.trpc.handler;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.TrpcChannel;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.Response;
import com.dewey.rpc.rpc.RpcInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther dewey
 * @date 2022/2/8 23:34
 */
@Slf4j
public class TrpcServerHandler implements Handler {
    private Invoker invoker;
    private Serialization serialization;
    @Override
    public void onReceive(TrpcChannel trpcChannel, Object message) throws Exception {
        RpcInvocation rpcInvocation = (RpcInvocation) message;
        System.out.println("收到rpcInvocation信息：" + rpcInvocation);
        //TODO 发起调用
        Object invoke = null;
        // 发出数据 -- response
        Response response = new Response();
        try {
            invoke = getInvoker().invoke(rpcInvocation);
            response.setStatus(0);
            response.setContent(invoke);
            System.out.println("服务端执行结果：" + invoke);
        } catch (Exception e) {
            response.setStatus(99);
            response.setContent(e.getMessage());
            e.printStackTrace();
        }
        //发送数据
        byte[] responseBody = getSerialization().serialize(response);
        trpcChannel.send(responseBody);//此时会触发write方法


    }

    @Override
    public void onWrite(TrpcChannel trpcChannel, Object message) throws Exception {

    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public Serialization getSerialization() {
        return serialization;
    }

    public void setSerialization(Serialization serialization) {
        this.serialization = serialization;
    }
}
