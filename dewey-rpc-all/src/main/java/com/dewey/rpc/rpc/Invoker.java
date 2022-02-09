package com.dewey.rpc.rpc;

/**
 * @auther dewey
 * @date 2022/2/9 21:32
 * 1、消费者调用服务，通过Invoker对象
 * 2、服务提供者调用 具体实现类，也通过Invoker对象
 */
public interface Invoker {
    /**
     * 返回接口
     * @return
     */
    Class getInterface();
    /**
     * 发起调用【负载均衡、容错、重连、都在这里面】
     */
    Object invoke(RpcInvocation rpcInvocation) throws Exception;
}
