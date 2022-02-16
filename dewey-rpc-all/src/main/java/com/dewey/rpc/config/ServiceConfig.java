package com.dewey.rpc.config;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther dewey
 * @date 2022/2/9 21:54
 */
@Data

public class ServiceConfig {
    private List<RegisterConfig> registerConfigs;
    private List<ProtocolConfig> protocolConfigs;
    private Class<?> service;
    private Object reference;
    private String version;
    public synchronized void addRegistryConfig(RegisterConfig registerConfig){
        if (CollectionUtils.isEmpty(registerConfigs)) {
            registerConfigs = new ArrayList<>();
        }
        this.registerConfigs.add(registerConfig);
    }
    public synchronized void addProtocolConfig(ProtocolConfig protocolConfig){
        if (CollectionUtils.isEmpty(protocolConfigs)) {
            protocolConfigs = new ArrayList<>();
        }
        this.protocolConfigs.add(protocolConfig);
    }
    public List<URI> toUri(){

        return null;
    }
}
