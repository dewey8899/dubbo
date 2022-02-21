package com.dewey.rpc.rpc.proxy;

import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @auther dewey
 * @date 2022/2/17 20:29
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private final Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        System.out.println("InvokerInvocationHandler invoke .... ");
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            }else if ("$destroy".equals(methodName)){
                return null;
            }else if ("hashCode".equals(methodName)){
                return invoker.hashCode();
            }else if ("hashCode".equals(methodName)){
                return invoker.hashCode();
            }
        }else if (parameterTypes.length == 1 && "equals".equals(methodName)){
            return invoker.equals(args[0]);
        }
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setMethodName(methodName);
        rpcInvocation.setArguments(args);
        rpcInvocation.setParameterTypes(parameterTypes);
        rpcInvocation.setServiceName(method.getDeclaringClass().getName());
        return invoker.invoke(rpcInvocation);
    }
}
