package com.dewey.rpc.rpc.cluster.loadbance;

import com.dewey.rpc.rpc.Invoker;
import com.dewey.rpc.rpc.cluster.LoadBalance;

import java.net.URI;
import java.util.Map;
import java.util.Random;

/**
 * @auther dewey
 * @date 2022/2/20 22:37
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public Invoker select(Map<URI, Invoker> invokerMap) {
        int index = new Random().nextInt(invokerMap.values().size());
        Invoker invoker = invokerMap.values().toArray(new Invoker[]{})[index];
        return invoker;
    }
}
