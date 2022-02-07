package com.dewey.rpc.remoting;

import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/7 22:14
 * 这里等于自己要处理编辑吗，各种玩意--注意线程安全问题
 * 不同的协议，要求实现这个接口
 */
public interface Codec {
    byte[] encode(Object msg) throws Exception;

    List<Object> decode(byte[] message) throws Exception;
}
