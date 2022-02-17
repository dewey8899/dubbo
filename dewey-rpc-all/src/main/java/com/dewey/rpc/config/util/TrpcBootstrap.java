package com.dewey.rpc.config.util;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.tools.ByteUtil;
import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.ReferenceConfig;
import com.dewey.rpc.config.RegisterConfig;
import com.dewey.rpc.config.ServiceConfig;
import com.dewey.rpc.registry.RegistryService;
import com.dewey.rpc.registry.redis.RedisRegistry;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.Protocol;
import com.dewey.rpc.rpc.proxy.ProxyFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
//            此处可选择具体网课设备
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
            protocol.export(exportUri,invoker);
            //注册到redis 中心
            for (RegisterConfig registerConfig : serviceConfig.getRegisterConfigs()) {
                URI registryUri = new URI(registerConfig.getAddress());
                //registryUri.getScheme() 指 "trpc.registry.address=RedisRegistry://127.0.0.1:6379" 中的  "RedisRegistry"
                RegistryService registryService = (RegistryService) SpiUtils.getServiceImpl(registryUri.getScheme(), RedisRegistry.class);
                registryService.init(registryUri);
                registryService.registry(exportUri);
            }
        }
    }

    /**
     * 创建一个代理对象
     * @param referenceConfig
     * @return
     */
    public static Object getReferenceBean(ReferenceConfig referenceConfig){
        try {
            //创建  -- 用于访问远程服务器的
            Invoker invoker = new Invoker() {
                @Override
                public Class getInterface() {
                    return null;
                }

                @Override
                public Object invoke(RpcInvocation rpcInvocation) throws Exception {
                    Serialization serialization = (Serialization) SpiUtils.getServiceImpl("JsonSerialization", Serialization.class);
                    byte[] requestBody = serialization.serialize(rpcInvocation);
                    //2、构建header
                    ByteBuf requestBuffer = Unpooled.buffer();
                    requestBuffer.writeByte(0xda);
                    requestBuffer.writeByte(0xbb);
                    requestBuffer.writeBytes(ByteUtil.int2bytes(requestBody.length));
                    requestBuffer.writeBytes(requestBody);
                    //3、发起请求
                    SocketChannel trpcClient = SocketChannel.open();
                    trpcClient.connect(new InetSocketAddress("127.0.0.1", 10088));
                    trpcClient.write(ByteBuffer.wrap(requestBuffer.array()));
                    //接受响应
                    ByteBuffer response = ByteBuffer.allocate(2048);
                    trpcClient.read(response);
                    System.out.println("响应内容：");
                    System.out.println(new String(response.array()));
                    return new String(response.array());
                }
            };
            //代理对象
            Object proxy = ProxyFactory.getProxy(invoker, new Class[]{referenceConfig.getService()});
            return proxy;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
