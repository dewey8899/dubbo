package com.dewey.rpc.rpc.proxy;

import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.RpcInvocation;

import java.lang.reflect.Method;

/**
 * @auther dewey
 * @date 2022/2/9 22:28
 */
public class ProxyFactory {
    public static Invoker getInvoker(Object proxy,Class type){
        return new Invoker() {
            @Override
            public Class getInterface() {
                return type;
            }

            @Override
            public Object invoke(RpcInvocation rpcInvocation) throws Exception {
                Method method = proxy.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParameterTypes());
                return method.invoke(proxy,rpcInvocation.getArguments());
            }
        };
    }
}
