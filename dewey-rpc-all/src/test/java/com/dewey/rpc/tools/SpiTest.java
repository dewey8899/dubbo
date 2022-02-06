package com.dewey.rpc.tools;

import com.dewey.rpc.common.serialize.Serialization;

import java.util.ServiceLoader;

/**
 * @auther dewey
 * @date 2022/2/5 22:08
 */
public class SpiTest {
    public static void main(String[] args) {
        ServiceLoader<Serialization> services = ServiceLoader.load(Serialization.class, Thread.currentThread().getContextClassLoader());
        for (Serialization service : services) {
            System.out.println(service.getClass().getSimpleName());
        }
    }
}
