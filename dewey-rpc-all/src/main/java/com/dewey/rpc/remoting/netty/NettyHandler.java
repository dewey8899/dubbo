package com.dewey.rpc.remoting.netty;

import com.dewey.rpc.remoting.Handler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @auther dewey
 * @date 2022/2/6 14:53
 * 处理网络请求内容
 */

public class NettyHandler extends ChannelDuplexHandler {
    private Handler handler;

    public NettyHandler(Handler handler) {
        this.handler = handler;
    }

    //入栈事件（收到数据 请求/响应）
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.onReceive(new NettyChannel(ctx.channel()),msg);
        System.out.println("收到内容" + msg);
//        super.channelRead(ctx, msg);
    }
}
