package com.dewey.rpc.config.util;

import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.config.ProtocolConfig;
import com.dewey.rpc.config.ServiceConfig;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.protocol.Protocol;
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
//          2.2  创建服务
            Protocol protocol = (Protocol) SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);
            protocol.export(exportUri,invoker);
        }
    }
}
