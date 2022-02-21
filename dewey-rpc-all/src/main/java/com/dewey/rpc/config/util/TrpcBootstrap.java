package com.dewey.rpc.config.util;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.ReferenceConfig;
import com.dewey.rpc.config.RegisterConfig;
import com.dewey.rpc.config.ServiceConfig;
import com.dewey.rpc.registry.RegistryService;
import com.dewey.rpc.registry.redis.RedisRegistry;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.cluster.ClusterInvoker;
import com.dewey.rpc.rpc.protocol.Protocol;
import com.dewey.rpc.rpc.protocol.trpc.TrpcProtocol;
import com.dewey.rpc.rpc.proxy.ProxyFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @auther dewey
 * @date 2022/2/9 22:10
 */
public class TrpcBootstrap {
    //暴露service服务
    public static void export(ServiceConfig serviceConfig) throws SocketException, URISyntaxException {
        // 1、构建一个代理对象（静态代理）
        Invoker invoker = ProxyFactory.getInvoker(serviceConfig.getReference(), serviceConfig.getService());
        //invoker对象
        //根据服务定义的协议，一次暴露。如果有多个协议那就暴露多次
        for (ProtocolConfig protocolConfig : serviceConfig.getProtocolConfigs()) {
            // 2.1 组织URL --协议://ip:端口/service全类名?参数名称=参数值&参数名称=参数2值
            StringBuilder stringBuilder = new StringBuilder();
//            此处可选择具体网络设备
            String hostAddress = NetworkInterface.getNetworkInterfaces()
                    .nextElement().getInterfaceAddresses().get(0)
                    .getAddress().getHostAddress();
            stringBuilder.append(protocolConfig.getName() + "://")
                    .append(hostAddress + ":")
                    .append(protocolConfig.getPort() + "/")
                    .append(serviceConfig.getService().getName()).append("?")
                    //版本号啥的不写了，意思一下吧
                    .append("transporter=").append(protocolConfig.getTransporter())
                    .append("&serialization=").append(protocolConfig.getSerialization());
            URI exportUri = new URI(stringBuilder.toString());
            System.out.println("准备暴露服务：" + exportUri);
//          2.2  创建服务 -- 多个service 用同一个端口 TODO 判断端口是不是复用了
            Protocol protocol = (Protocol) SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);
            assert protocol != null;
            protocol.export(exportUri,invoker);
            //注册到redis 中心
            for (RegisterConfig registerConfig : serviceConfig.getRegisterConfigs()) {
                URI registryUri = new URI(registerConfig.getAddress());
                //registryUri.getScheme() 指 "trpc.registry.address=RedisRegistry://127.0.0.1:6379" 中的  "RedisRegistry"
                RegistryService registryService = (RegistryService) SpiUtils.getServiceImpl(registryUri.getScheme(), RegistryService.class);
                assert null != registryService;
                registryService.init(registryUri);
                registryService.registry(exportUri);
            }
        }
    }

    /**
     * 创建一个代理对象
     * @param referenceConfig 引用配置
     * @return 返回一个代理对象
     */
    public static Object getReferenceBean(ReferenceConfig referenceConfig){
        try {
//            TrpcProtocol trpcProtocol = new TrpcProtocol();
//            Invoker invoker = trpcProtocol.refer(new URI("TrpcProtocal://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization"));

            //根据服务通过注册中心，找到服务提供者实例
            ClusterInvoker clusterInvoker = new ClusterInvoker(referenceConfig);
            //代理对象
            return ProxyFactory.getProxy(clusterInvoker, new Class[]{referenceConfig.getService()});
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
