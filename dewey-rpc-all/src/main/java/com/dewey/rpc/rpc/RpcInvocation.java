package com.dewey.rpc.rpc;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther dewey
 * @date 2022/2/7 21:51
 * 保留一次调用相关的目的地、参数、每次都有唯一的ID
 */
@Data
@ToString
public class RpcInvocation implements Serializable {
    static AtomicLong SEQ = new AtomicLong(0);
    private Long id;
    private String methodName;
    private String serviceName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;

    public RpcInvocation() {
        //初始化一个ID
        this.setId(SEQ.incrementAndGet());
    }
}
