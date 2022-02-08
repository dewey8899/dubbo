package com.dewey.rpc.rpc.protocol;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/8 22:38
 * 协议
 */
public interface Protocol {
    /**
     *
     * @param exportUrl 协议名称://IP:端口/service全类名?参数名称=参数值&参数名称=参数2值
     */
    void export(URI exportUrl);
}
