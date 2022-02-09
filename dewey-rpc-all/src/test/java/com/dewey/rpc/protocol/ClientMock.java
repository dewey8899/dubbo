package com.dewey.rpc.protocol;

import com.dewey.rpc.common.serialize.Serialization;
import com.dewey.rpc.common.tools.ByteUtil;
import com.dewey.rpc.common.tools.SpiUtils;
import com.dewey.rpc.rpc.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @auther dewey
 * @date 2022/2/9 20:13
 * 具体传输内容【协议头（标记、业务body长度）】【业务body (RpcInvocation/Response)】
 * TRPCProtocol
 * 0xdabb（两个字节） + body长度（6字节） + body（默认json序列化）
 */
public class ClientMock {
    public static void main(String[] args) throws Exception {
        //1、构建body
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setServiceName("com.study.dubbo.sms.api.SmsService");
        rpcInvocation.setMethodName("send");
        rpcInvocation.setParameterTypes(new Class[]{String.class,String.class});
        rpcInvocation.setArguments(new Object[]{"10086","短信"});
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
        trpcClient.connect(new InetSocketAddress("127.0.0.1", 8080));
        trpcClient.write(ByteBuffer.wrap(requestBuffer.array()));
        //接受响应
        ByteBuffer response = ByteBuffer.allocate(1025);
        trpcClient.read(response);
        System.out.println("响应内容：");
        System.out.println(new String(response.array()));

    }
}
