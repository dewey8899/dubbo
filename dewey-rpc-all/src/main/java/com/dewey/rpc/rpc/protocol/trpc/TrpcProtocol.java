package com.dewey.rpc.rpc.protocol.trpc;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.serialize.json.JsonSerialization;
import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.common.tools.URIUtils;
import com.dewey.rpc.remoting.Transporter;
import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.Protocol;
import com.dewey.rpc.rpc.protocol.trpc.codec.TrpcCodec;
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
        //3、底层网络框架
        String transporterName = URIUtils.getParam(exportUrl, "transporter");
        Transporter transporter = (Transporter) SpiUtils.getServiceImpl(transporterName, Transporter.class);
        transporter.start(exportUrl, trpcCodec, trpcServerHandler);
    }
}
