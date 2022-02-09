package com.dewey.rpc.common.tools;

import com.dewey.rpc.common.serialize.Serialization;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @auther dewey
 * @date 2022/2/5 22:38
 */
public class SpiUtils {
    public static Object getServiceImpl(String serviceName,Class clazz){
        ServiceLoader<?> services = ServiceLoader.load(clazz,Thread.currentThread().getContextClassLoader());
        //根据服务定义的协议，一次暴露，如果有多个协议那就暴露多次
        for (Object s : services) {
            if (s.getClass().getSimpleName().equals(serviceName)){
                return s;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Serialization serviceImpl = (Serialization) SpiUtils.getServiceImpl("JsonSerialization", Serialization.class);
        String name = serviceImpl.getClass().getSimpleName();
        System.out.println(name);
    }
}
