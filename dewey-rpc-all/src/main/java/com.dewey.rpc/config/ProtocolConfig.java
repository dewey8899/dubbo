package com.dewey.rpc.config;

import lombok.Data;

/**
 * @auther dewey
 * @date 2022/2/6 15:14
 * 协议相关配置
 */
@Data
public class ProtocolConfig {
    private String name;
    private String port;
    private String host;
    private String serialization;
    private String transporter;//网络底层实现使用的是netty或者mina
}
