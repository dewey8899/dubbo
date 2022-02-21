package com.dewey.rpc.registry;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @auther dewey
 * @date 2022/2/16 22:40
 */
public interface RegistryService {
    /**
     * 注册
     * @param uri
     */
    void registry(URI uri);

    /**
     * 订阅所有的服务
     * @param service
     * @param notifyListener
     */
    void subscribe(String service, NotifyListener notifyListener) throws URISyntaxException, InterruptedException;

    /**
     * 配置连接信息
     * @param address
     */
    void init(URI address);
}
