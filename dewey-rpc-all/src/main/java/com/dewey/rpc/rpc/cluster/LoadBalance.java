package com.dewey.rpc.rpc.cluster;

import com.dewey.rpc.rpc.Invoker;

import java.net.URI;
import java.util.Map;

/**
 * @auther dewey
 * @date 2022/2/20 22:38
 */
public interface LoadBalance {
    Invoker select(Map<URI, Invoker> invokerMap);
}
