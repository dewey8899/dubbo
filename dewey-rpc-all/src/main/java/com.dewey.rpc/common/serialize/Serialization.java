package com.dewey.rpc.common.serialize;

/**
 * @auther dewey
 * @date 2022/2/5 21:49
 */

/**
 * 从字节数组到java对象的互相转换 的接口
 */
public interface Serialization {
    byte[] serialize(Object output) throws Exception;

    Object deserialize(byte[] input, Class clazz) throws Exception;
}

