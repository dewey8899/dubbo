package com.dewey.rpc.remoting;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.remoting.netty.Netty4Transporter;
import com.dewey.rpc.rpc.RpcInvocation;
import com.dewey.rpc.rpc.protocol.trpc.codec.TrpcCodec;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/7 22:35
 * 测试步骤：
 * 1、启动main方法
 * 2、在cmd中使用 ---> curl http://127.0.0.1:8080/XXX   XXX代表任意输入的内容
 */
public class Netty4TransporterTest {
    public static void main(String[] args) throws URISyntaxException {
        new Netty4Transporter().start(new URI("trpp://127.0.0.1:8080"),
                new Codec() {
                    @Override
                    public byte[] encode(Object msg) throws Exception {
                        return new byte[0];
                    }

                    @Override
                    public List<Object> decode(byte[] message) throws Exception {
                        List<Object> objects = new ArrayList<>();
                        System.out.println("打印请求的内容：" + new String(message));
                        objects.add("1:"+new String(message));
                        objects.add("2:"+new String(message));
                        objects.add("3:"+new String(message));
                        return objects;
                    }

                    @Override
                    public Codec createInstance() {
                        TrpcCodec trpcCodec = new TrpcCodec();
                        trpcCodec.setDecodeType(RpcInvocation.class);
                        trpcCodec.setSerialization((Serialization) SpiUtils.getServiceImpl("JsonSerialization", Serialization.class));
                        return trpcCodec;
                    }
                }, new Handler() {
                    @Override
                    public void onReceive(TrpcChannel trpcChannel, Object message) throws Exception {
                        System.out.println("onReceiveHandler：" + message);
                    }

                    @Override
                    public void onWrite(TrpcChannel trpcChannel, Object message) throws Exception {

                    }
                });
    }
}
