package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/7 22:07
 * 进行编解码【只是定义，不做具体的协议】
 * 接收端角度：【把请求中的网络字节数据，转成java对象】
 * 发送端角度：【发送的数据以特定的协议格式进行发送】
 */
public class NettyCodec extends ChannelDuplexHandler {
    private Codec codec;

    public NettyCodec(Codec codec) {
        this.codec = codec;
    }

    //入栈事件（收到数据 请求/响应）
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //解码
        //1、读取数据
        ByteBuf data = (ByteBuf) msg;
        byte[] dataBytes = new byte[data.readableBytes()];
        data.readBytes(dataBytes);
        // 2、格式转 --
        List<Object> out = codec.decode(dataBytes);
        //3、 处理器继续处理 -决定下一个处理器 处理数据的次数
        for (Object o : out) {
            ctx.fireChannelRead(o);
        }
        System.out.println("内容：" + msg);
    }
}
