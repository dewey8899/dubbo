package com.dewey.rpc.rpc.protocol.trpc;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.common.tools.URIUtils;
import com.dewey.rpc.remoting.Client;
import com.dewey.rpc.remoting.Transporter;
import com.dewey.rpc.remoting.netty.NettyClient;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.Response;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.Protocol;
import com.dewey.rpc.rpc.protocol.trpc.codec.TrpcCodec;
import com.dewey.rpc.rpc.protocol.trpc.handler.TrpcClientHandler;
import com.dewey.rpc.rpc.protocol.trpc.handler.TrpcServerHandler;

import java.net.URI;

/**
 * @auther dewey
 * @date 2022/2/8 22:49
 */
public class TrpcProtocol implements Protocol {

    //导出-- 创建服务
    @Override
    public void export(URI exportUrl, Invoker invoker) {
        //找到序列化器
        String serializationName = URIUtils.getParam(exportUrl, "serialization");
        Serialization serialization = (Serialization) SpiUtils.getServiceImpl(serializationName, Serialization.class);
        //1、编解码器
        TrpcCodec trpcCodec = new TrpcCodec();
        trpcCodec.setDecodeType(RpcInvocation.class);
        trpcCodec.setSerialization(serialization);

        //2、收到请求处理器
        TrpcServerHandler trpcServerHandler = new TrpcServerHandler();
        trpcServerHandler.setInvoker(invoker);
        trpcServerHandler.setSerialization(serialization);
        //3、底层网络框架
        String transporterName = URIUtils.getParam(exportUrl, "transporter");
        Transporter transporter = (Transporter) SpiUtils.getServiceImpl(transporterName, Transporter.class);
        assert transporter != null;
        transporter.start(exportUrl, trpcCodec, trpcServerHandler);
    }

    @Override
    public Invoker refer(URI consumerUri) throws InterruptedException {
        //1、找到序列化
        String serializationName = URIUtils.getParam(consumerUri, "serialization");
        Serialization serialization = (Serialization) SpiUtils.getServiceImpl(serializationName, Serialization.class);
        //2、编解码器
        TrpcCodec codec = new TrpcCodec();
        codec.setSerialization(serialization);
        codec.setDecodeType(Response.class);//客户端解码，服务端发送过来的响应
        //3、收到响应 处理
        TrpcClientHandler trpcClientHandler = new TrpcClientHandler();
        //4、连接---长链接
        String transporterName = URIUtils.getParam(consumerUri, "transporter");
        Transporter transporter = (Transporter) SpiUtils.getServiceImpl(transporterName, Transporter.class);
        Client client = transporter.connect(consumerUri, codec, trpcClientHandler);
        //5、创建一个invoker  通过网络连接发送数据

        TrpcClientInvoker trpcClientInvoker = new TrpcClientInvoker(client,serialization);

        return trpcClientInvoker;
    }
}
