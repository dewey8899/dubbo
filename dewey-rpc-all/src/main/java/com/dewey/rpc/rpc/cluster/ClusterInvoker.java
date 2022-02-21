package com.dewey.rpc.rpc.cluster;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ReferenceConfig;
import com.dewey.rpc.config.RegisterConfig;
import com.dewey.rpc.registry.RegistryService;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.Protocol;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther dewey
 * @date 2022/2/20 21:41
 * 集群环境下的Invoker -- 包括一个服务的多个实例用
 */
public class ClusterInvoker implements Invoker {
    ReferenceConfig referenceConfig;
    /**
     * 代表这个服务能够调用的所有实例
     */
    Map<URI, Invoker> invokers = new ConcurrentHashMap<>();
    LoadBalance loadBalance;
    @SneakyThrows
    public ClusterInvoker(ReferenceConfig referenceConfig){
        this.referenceConfig = referenceConfig;
        loadBalance = (LoadBalance) SpiUtils.getServiceImpl(referenceConfig.getLoadBalance(), LoadBalance.class);
        //接口类的全类名
        String serviceName = referenceConfig.getService().getName();
        //1、服务发现 -- 注册中心
        //TrpcProtocal://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
        List<RegisterConfig> registerConfigs = referenceConfig.getRegisterConfigs();
        for (RegisterConfig registerConfig : registerConfigs) {
            URI registerUri = new URI(registerConfig.getAddress());
            RegistryService registryService = (RegistryService) SpiUtils.getServiceImpl(registerUri.getScheme(), RegistryService.class);
            registryService.init(registerUri);
            registryService.subscribe(serviceName, uriSet -> {
                System.out.println("当前的服务invoker信息：" + invokers);
                //剔除服务 -- 创建号的invoker,是不是存在于最小的实例里面
                for (URI uri : uriSet) {
                    if (!invokers.containsKey(uri)) {
                        invokers.remove(uri);
                    }
                }
                //新增 - 意味着新建一个invoker
                for (URI uri : uriSet) {
                    //意味着有一个服务实例
                    if (!invokers.containsKey(uri)) {
                        Protocol protocol = (Protocol) SpiUtils.getServiceImpl(uri.getScheme(), Protocol.class);
                        Invoker invoker = protocol.refer(uri);//代表一个长连接
                        //保存起来
                        invokers.putIfAbsent(uri, invoker);
                    }
                }
            });
        }
    }

    //proxy + invoker = 完整代理
    @Override
    public Class getInterface() {
        return referenceConfig.getService();
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        // invoker 调用一次 -- 这么多Invokers 调用哪一个
        Invoker select = loadBalance.select(invokers);
        Object result = select.invoke(rpcInvocation);
        return result;
    }
}
