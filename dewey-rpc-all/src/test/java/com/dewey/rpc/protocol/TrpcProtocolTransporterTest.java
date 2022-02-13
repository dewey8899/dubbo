package com.dewey.rpc.protocol;

import com.dewey.rpc.common.serialize.json.JsonSerialization;
import com.dewey.rpc.remoting.Codec;
import com.dewey.rpc.remoting.Handler;
import com.dewey.rpc.remoting.TrpcChannel;
import com.dewey.rpc.remoting.netty.Netty4Transporter;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.trpc.codec.TrpcCodec;
import com.dewey.rpc.rpc.protocol.trpc.handler.TrpcServerHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/7 22:35
 * 集成了trpc 一套协议处理机制
 * 测试步骤：
 * 1、启动这个 main方法
 * 2、启动ClientMock，然后在这个方法的控制台查看信息
 */
public class TrpcProtocolTransporterTest {
    public static void main(String[] args) throws URISyntaxException {
        TrpcCodec trpcCodec = new TrpcCodec();
        trpcCodec.setDecodeType(RpcInvocation.class);
        trpcCodec.setSerialization(new JsonSerialization());
        TrpcServerHandler trpcServerHandler = new TrpcServerHandler();
        new Netty4Transporter().start(new URI("trpp://127.0.0.1:8080"),
                trpcCodec, trpcServerHandler);
    }
}
