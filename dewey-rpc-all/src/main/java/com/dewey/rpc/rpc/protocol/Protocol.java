package com.dewey.rpc.rpc.protocol;

import com.dewey.rpc.rpc.Invoker;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/8 22:38
 * 协议
 */
public interface Protocol {
    /**
     * 开放服务
     * @param exportUrl 协议名称://IP:端口/service全类名?参数名称=参数值&参数名称=参数2值
     * @param invoker 调用具体实现类的对象
     */
    void export(URI exportUrl, Invoker invoker);

    /**
     * 获取一个协议所对应的invoker,用于调用
     * @param consumerUri
     * @return
     */
    Invoker refer(URI consumerUri) throws InterruptedException;
}
