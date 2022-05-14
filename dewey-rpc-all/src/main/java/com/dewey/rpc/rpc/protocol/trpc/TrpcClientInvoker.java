package com.dewey.rpc.rpc.protocol.trpc;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.remoting.Client;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.Response;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.trpc.handler.TrpcClientHandler;

import java.util.Map;
import java.util.concurrent.*;

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
        } else {
            throw new Exception("server error:" + response.getContent().toString());
        }
    }

    private static final Map<Long, CompletableFuture> invokerMap = new ConcurrentHashMap<>();

    public static CompletableFuture waitResult(long messageId) {
        CompletableFuture future = new CompletableFuture();
        invokerMap.put(messageId, future);
        return future;
    }
    // 处理业务
    public static void doWhat(Long requestId,Object message) throws InterruptedException {
        Response response = new Response();
        response.setContent(message);
        response.setRequestId(requestId);
        Thread.sleep(1000);
        //接受所有的结果  -- 和invoker 调用者不在一个线程
        //根据id 和 具体的请求对应起来 complete发送结果
        invokerMap.get(response.getRequestId()).complete(response);
        invokerMap.remove(requestId);
    }
    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            CompletableFuture completableFuture = waitResult(2);
            //future get 获取结果
            Object result = null;
            try {
                result = completableFuture.get(1, TimeUnit.SECONDS);
                Response response = (Response) result;
                System.out.println(response.getContent());
                System.out.println( "2L -->" + Thread.currentThread().getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
        doWhat(2L,"dewey set response2");

        new Thread(()->{
            CompletableFuture completableFuture = waitResult(3);
            //future get 获取结果
            Object result = null;
            try {
                result = completableFuture.get(1, TimeUnit.SECONDS);
                Response response = (Response) result;
                System.out.println(response.getContent());
                System.out.println( "3L -->" + Thread.currentThread().getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
        doWhat(3L,"dewey set response3");

    }

}